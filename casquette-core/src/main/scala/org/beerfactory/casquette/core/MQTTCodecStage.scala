package org.beerfactory.casquette.core

import akka.stream.{Attributes, Outlet, Inlet, BidiShape}
import akka.stream.stage.{OutHandler, InHandler, GraphStageLogic, GraphStage}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import org.beerfactory.casquette.mqtt.MQTTPacket
import scodec.Attempt.{Failure, Successful}
import scodec.Codec
import scodec.Err.InsufficientBits
import scodec.bits.{ByteVector, BitVector}

/**
  * Created by njouanin on 29/02/16.
  */
class MQTTCodecStage extends GraphStage[BidiShape[ByteString, MQTTPacket, MQTTPacket, ByteString]] with LazyLogging {
  val in1 = Inlet[ByteString]("MQTTCodecStage.in1")
  val out1 = Outlet[MQTTPacket]("MQTTCodecStage.out1")
  val in2 = Inlet[MQTTPacket]("MQTTCodecStage.in2")
  val out2 = Outlet[ByteString]("MQTTCodecStage.out2")
  val shape = BidiShape(in1, out1, in2, out2)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = {
    var buffer = BitVector.empty

    new GraphStageLogic(shape) {
      setHandler(in1, new InHandler {
        override def onPush(): Unit = {
          val bytes = grab(in1)
          buffer = buffer ++ BitVector.view(bytes.toByteBuffer)
          push_decoded(push(out1,_))
        }
        override def onUpstreamFinish(): Unit =
          {
            while(!buffer.isEmpty)
            {
              push_decoded(emit(out1,_))
            }
            complete(out1)
          }
      })
      setHandler(in2, new InHandler {
        override def onPush():Unit = {
          val message = grab(in2)
          push_encoded(message)
        }
        override def onUpstreamFinish(): Unit = complete(out2)
      })
      setHandler(out1, new OutHandler {
        override def onPull(): Unit = {
          if(!isClosed(in1))
            pull(in1)
          else
            push_decoded(push(out1,_))
        }
      })
      setHandler(out2, new OutHandler {
        override def onPull(): Unit = {
          if(!isClosed(in2))
            pull(in2)
        }
      })

      private def push_encoded(message:MQTTPacket): Unit = {
        Codec[MQTTPacket].encode(message) match {
          case Successful(value) => push(out2, ByteString(value.toByteBuffer))
          case Failure(cause) => logger.warn(s"Error while encoding message $message: $cause")
        }
      }

      private def push_decoded(f:(MQTTPacket) => Unit): Unit = {
        if(!buffer.isEmpty) {
          Codec[MQTTPacket].decode(buffer) match {
            case Successful(result) =>
              f(result.value)
              buffer = result.remainder
            case Failure(cause) => cause match {
              case _: InsufficientBits ⇒
                // Not enough bytes have been read to decode length -> pull more
                if (isClosed(in1))
                  complete(out1)
                else
                  pull(in1)
              case _ ⇒ failStage(new MQTTFrameParserException("Failed to decoded incoming data frame", Some(cause)))
            }
          }
        }
      }
    }
  }
}
