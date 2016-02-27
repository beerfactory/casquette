package org.beerfactory.casquette.client

import java.net.{MalformedURLException, URI, InetSocketAddress, InetAddress}

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import org.beerfactory.casquette.core.CommonToolsActor

import scala.util.{Failure, Success, Try}

/**
  * Created by nico on 19/02/2016.
  */
class MQTTClientActor extends Actor with ActorLogging {
  val commonToolsActor = context.actorOf(CommonToolsActor.props(), "commonTools")
  override def receive: Receive = notConnected

  private def notConnected(): Receive = LoggingReceive {
    case Status => sender() ! Disconnected
    //case c:Connect ⇒ doConnect(c)
  }

  private[client] def getHostPort(uri: URI): Try[(String, Int)] = {
    if (!List("mqtt", "mqtts", "ws", "wss").contains(uri.getScheme))
      Failure(new MalformedURLException(s"Invalid protocol $uri.getScheme()"))
    else {
      val port = uri.getPort match {
        case p if p > -1 => p
        case _ => uri.getScheme match {
          case "mqtt" => context.system.settings.config.getInt("casquette.default.tcp-port.mqtt")
          case "mqtts" => context.system.settings.config.getInt("casquette.default.tcp-port.mqtts")
          case "ws" => context.system.settings.config.getInt("casquette.default.tcp-port.ws")
          case "wss" => context.system.settings.config.getInt("casquette.default.tcp-port.wss")
        }
      }
      Success[(String, Int)]((uri.getHost, port))
    }
  }
}
