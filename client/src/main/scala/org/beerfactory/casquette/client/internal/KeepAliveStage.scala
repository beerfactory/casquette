package org.beerfactory.casquette.client.internal

import akka.stream.{Attributes, BidiShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import org.beerfactory.casquette.mqtt.MQTTPacket

/**
  * Created by nico on 13/03/2016.
  */
class KeepAliveStage extends GraphStage[BidiShape[MQTTPacket, MQTTPacket, MQTTPacket, MQTTPacket]]{
  val incomingIn: Inlet[MQTTPacket] = Inlet("KeepAliveStage.incoming.in")
  val incomingOut: Outlet[MQTTPacket] = Outlet("KeepAliveStage.incoming.out")
  val outgoingIn: Inlet[MQTTPacket] = Inlet("KeepAliveStage.outgoing.in")
  val outgoingOut: Outlet[MQTTPacket] = Outlet("KeepAliveStage.outgoing.out")

  override def shape: BidiShape[MQTTPacket, MQTTPacket, MQTTPacket, MQTTPacket] =
    BidiShape(incomingIn, incomingOut, outgoingIn, outgoingOut)
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    setHandler(incomingIn, new InHandler {
      @scala.throws[Exception](classOf[Exception])
      override def onPush(): Unit = ???
    })
    setHandler(incomingOut, new OutHandler {
      @scala.throws[Exception](classOf[Exception])
      override def onPull(): Unit = ???
    })
  }

}
