package org.beerfactory.casquette.client

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive

import scala.util.Random

/**
  * Created by nico on 19/02/2016.
  */
class MQTTClientActor extends Actor with ActorLogging {
  override def receive: Receive = notConnected

  private def notConnected(): Receive = LoggingReceive {
    case Status => sender() ! Disconnected
    case c:Connect ⇒ doConnect(c)
  }

  private def doConnect(c: Connect) = {

  }
}
