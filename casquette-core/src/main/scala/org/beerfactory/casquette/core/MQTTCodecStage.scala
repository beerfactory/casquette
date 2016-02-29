package org.beerfactory.casquette.core

import akka.stream.{Attributes, Outlet, Inlet, BidiShape}
import akka.stream.stage.{OutHandler, InHandler, GraphStageLogic, GraphStage}
import akka.util.ByteString
import org.beerfactory.casquette.mqtt.MQTTPacket
import scodec.Codec
import scodec.bits.{ByteVector, BitVector}

/**
  * Created by njouanin on 29/02/16.
  */
class MQTTCodecStage extends GraphStage[BidiShape[ByteString, MQTTPacket, MQTTPacket, ByteString]] {
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
          decode()
        }
        override def onUpstreamFinish(): Unit =
          {
            ???
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
        override def onPull(): Unit = ???
      })
      setHandler(out2, new OutHandler {
        override def onPull(): Unit = ???
      })

      private def decode(): Unit = ???

      private def push_encoded(message:MQTTPacket): Unit = {
        val result = Codec[MQTTPacket].encode(message)
        if(result.isSuccessful)
          push(out2, ByteString(result.require.toByteBuffer))
        else
          {
            //TODO: Report error
          }
      }
    }
  }
}
