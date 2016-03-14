package org.beerfactory.casquette.client.internal

import akka.stream.{Attributes, BidiShape, Inlet, Outlet}
import akka.stream.stage._
import com.typesafe.scalalogging.LazyLogging
import org.beerfactory.casquette.mqtt.{MQTTPacket, PingReqPacket, PingRespPacket}

import scala.concurrent.duration.{Deadline, FiniteDuration}

/**
  * Created by nico on 13/03/2016.
  */
class KeepAliveStage(maxIdle: FiniteDuration)
  extends GraphStage[BidiShape[MQTTPacket, MQTTPacket, MQTTPacket, MQTTPacket]] with LazyLogging {

  val incomingIn: Inlet[MQTTPacket] = Inlet("KeepAliveStage.incoming.in")
  val incomingOut: Outlet[MQTTPacket] = Outlet("KeepAliveStage.incoming.out")
  val outgoingIn: Inlet[MQTTPacket] = Inlet("KeepAliveStage.outgoing.in")
  val outgoingOut: Outlet[MQTTPacket] = Outlet("KeepAliveStage.outgoing.out")
  override def initialAttributes = Attributes.name("KeepAliveStage")

  override def shape: BidiShape[MQTTPacket, MQTTPacket, MQTTPacket, MQTTPacket] =
    BidiShape(incomingIn, incomingOut, outgoingIn, outgoingOut)
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {
    private val timerName = "keepAliveTimer"
    private var nextDeadline = Deadline.now + maxIdle
    private var pendingPingRequest = false

    // Prefetching to ensure priority of actual upstream elements
    override def preStart(): Unit = pull(incomingIn)

    setHandler(incomingIn, new InHandler {
      override def onPush(): Unit = {
        val packet = grab(incomingIn)
        if(!pendingPingRequest)
          push(incomingOut, packet) //No Ping request pending for keepalive => forward packet
        else {
          packet match {
            case _:PingRespPacket => pendingPingRequest = false // Ping response from keep alive, drop element
            case _ => push(incomingOut, packet)
          }
        }
      }
      override def onUpstreamFinish(): Unit = {
        if (!isAvailable(incomingIn))
          completeStage()
      }
    })

    setHandler(incomingOut, new OutHandler {
      override def onPull(): Unit = pull(incomingIn)
    })

    setHandler(outgoingIn, new InHandler {
      override def onPush(): Unit = {
        nextDeadline = Deadline.now + maxIdle
        cancelTimer(timerName)
        if (isAvailable(outgoingOut)) {
          push(outgoingOut, grab(outgoingIn))
          pull(outgoingIn)
        }
      }
      override def onUpstreamFinish(): Unit = {
        if (!isAvailable(outgoingIn))
          completeStage()
      }
    })
    setHandler(outgoingOut, new OutHandler {
      override def onPull(): Unit = {
        if (isAvailable(outgoingIn)) {
          push(outgoingOut, grab(outgoingIn))
          if (isClosed(outgoingIn))
            completeStage()
          else
            pull(outgoingIn)
        }
        else {
          if (nextDeadline.isOverdue()) {
            ping_keepalive()
          }
          else
            scheduleOnce(timerName, nextDeadline.timeLeft)
        }
      }
    })

    override protected def onTimer(timerKey: Any): Unit = {
      if (nextDeadline.isOverdue() && isAvailable(outgoingOut)) {
        ping_keepalive()
      }
    }

    private def ping_keepalive(): Unit = {
      if(pendingPingRequest)
        logger.warn("Ping Response still pending. Re-sending Ping Request")
      push(outgoingOut, PingReqPacket())
      pendingPingRequest = true
      nextDeadline = Deadline.now + maxIdle
    }
  }
}
