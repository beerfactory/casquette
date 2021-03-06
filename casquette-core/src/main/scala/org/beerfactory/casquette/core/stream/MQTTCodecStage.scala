package org.beerfactory.casquette.core.stream

import akka.stream._
import akka.stream.scaladsl.BidiFlow
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import org.beerfactory.casquette.mqtt.MQTTPacket
import scodec.Attempt.{Failure, Successful}
import scodec.Err.InsufficientBits
import scodec.bits.BitVector
import scodec.{Codec, Err}

/**
  * Created by njouanin on 29/02/16.
  */
object MQTTCodecStage {
  def apply() = BidiFlow.fromFlows(new MQTTDecodeStage, new MQTTEncodeStage)
}

class MQTTDecodeStageException(msg: String, error: Option[Err]) extends Exception(msg)

class MQTTDecodeStage extends GraphStage[FlowShape[ByteString, MQTTPacket]] with LazyLogging {
  val in = Inlet[ByteString]("MQTTDecodeStage.in")
  val out = Outlet[MQTTPacket]("MQTTDecodeStage.out")
  val shape = FlowShape.of(in, out)

  override def createLogic(attributes: Attributes): GraphStageLogic = {
    var buffer = BitVector.empty
    new GraphStageLogic(shape) {
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val bytes = grab(in)
          buffer = buffer ++ BitVector.view(bytes.toByteBuffer)
          push_decoded(push(out,_))
        }
        override def onUpstreamFinish(): Unit =
        {
          while(!buffer.isEmpty)
          {
            push_decoded(emit(out,_))
          }
          complete(out)
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          if(!isClosed(in))
            pull(in)
          else
            push_decoded(push(out,_))
        }
      })

      private def push_decoded(f:(MQTTPacket) => Unit): Unit = {
        if(!buffer.isEmpty) {
          Codec[MQTTPacket].decode(buffer) match {
            case Successful(result) =>
              f(result.value)
              buffer = result.remainder
            case Failure(cause) => cause match {
              case _: InsufficientBits ⇒
                // Not enough bytes have been read to decode length -> pull more
                if (isClosed(in))
                  complete(out)
                else
                  pull(in)
              case _ ⇒ failStage(new MQTTDecodeStageException("Failed to decoded incoming data frame", Some(cause)))
            }
          }
        }
      }
    }
  }
}

class MQTTEncodeStage extends GraphStage[FlowShape[MQTTPacket, ByteString]] with LazyLogging {
  val in = Inlet[MQTTPacket]("MQTTEncodeStage.in")
  val out = Outlet[ByteString]("MQTTEncodeStage.out")
  val shape = FlowShape.of(in, out)

  override def createLogic(attributes: Attributes): GraphStageLogic = {
    new GraphStageLogic(shape) {
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val message = grab(in)
          push_encoded(message)
        }
        override def onUpstreamFinish(): Unit = complete(out)
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          if(!isClosed(in))
            pull(in)
        }
      })

      private def push_encoded(message:MQTTPacket): Unit = {
        Codec[MQTTPacket].encode(message) match {
          case Successful(value) => push(out, ByteString(value.toByteBuffer))
          case Failure(cause) => logger.warn(s"Error while encoding message $message: $cause")
        }
      }
    }
  }
}