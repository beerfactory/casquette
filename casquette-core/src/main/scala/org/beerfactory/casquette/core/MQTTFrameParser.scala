package org.beerfactory.casquette.core

import akka.stream.{Attributes, Inlet, Outlet, FlowShape}
import akka.stream.stage.{InHandler, OutHandler, GraphStageLogic, GraphStage}
import akka.util.ByteString
import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.beerfactory.casquette.mqtt.codecs.MQTTCodecs
import scodec.Err.InsufficientBits
import scodec.{Err, DecodeResult, Codec}
import scodec.bits.{BitVector, ByteVector}
import scodec.codecs._


class MQTTFrameParserException(message: String, err: Option[Err]=None) extends Exception(message)

/**
  * Created by nico on 27/02/2016.
  */
class MQTTFrameParser extends GraphStage[FlowShape[ByteString, ByteVector]] with LazyLogging {

  val in = Inlet[ByteString]("MQTTFrameParser.in")
  val out = Outlet[ByteVector]("MQTTFrameParser.out")
  override val shape = FlowShape.of(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    val headerCodec: Codec[(Byte, Int)] = byte ~ MQTTCodecs.remainingLengthCodec
    var buffer = ByteVector.empty
    var needed = -1
    var headerBytes = ByteVector.empty

    new GraphStageLogic(shape) {
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          logger.info("onPull()")
          if (isClosed(in)) run()
          else pull(in)
        }
      })

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          logger.info("onPush()")
          val bytes = grab(in)
          buffer = buffer ++ ByteVector.view(bytes.toByteBuffer)
          run()
        }

        override def onUpstreamFinish(): Unit = {
          logger.info(s"onUpstreamFinish(), buffer=$buffer")
          while (!buffer.isEmpty) {
            needed = headerCodec.decode(buffer.bits).fold(decodeHeaderFailure, decodeHeaderSuccess)
            val frame = nextFrame()
            logger.info(s"emit $frame")
            emit(out, frame)
          }
          complete(out)
        }
      })

      private def decodeHeaderSuccess(result: DecodeResult[(Byte, Int)]): Int = {
        val (_, length) = result.value
        val remainder = result.remainder.toByteVector
        headerBytes = buffer.take((buffer.length - remainder.length).toInt)
        buffer = remainder
        length //return number of bytes needed to complete packet frame
      }

      private def decodeHeaderFailure(err: Err): Int = {
        err match {
          case _: InsufficientBits ⇒
            // Not enough bytes have been read to decode length -> pull more
            if (isClosed(in))
              completeStage()
            else
              pull(in)
          case _ ⇒ failStage(new MQTTFrameParserException("Failed to decoded incoming data frame", Some(err)))
        }
        -1
      }

      private def nextFrame():ByteVector = {
        val emit = headerBytes ++ buffer.take(needed)
        buffer = buffer.drop(needed)
        needed = -1
        headerBytes = ByteVector.empty
        emit
      }

      private def run(): Unit = {
        if (needed == -1) {
          // decode first bytes to read needed length
          needed = headerCodec.decode(buffer.bits).fold(decodeHeaderFailure, decodeHeaderSuccess)
        }
        if (buffer.length < needed) {
          logger.info(s"${buffer.length} bytes in buffer, need $needed to complete MQTT message")
          if (!isClosed(in))
            pull(in)
          else
            failStage(new MQTTFrameParserException("Premature end of upstream"))
        }
        else {
          val frame = nextFrame()
          push(out, frame)
          logger.info(s"push $frame")
        }
      }
    }
  }
}