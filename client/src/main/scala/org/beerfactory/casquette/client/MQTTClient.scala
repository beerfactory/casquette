package org.beerfactory.casquette.client

import java.net.{MalformedURLException, URI}
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source, Tcp}
import com.typesafe.scalalogging.LazyLogging
import org.beerfactory.casquette.core.stream.stats.StatFlow
import org.beerfactory.casquette.mqtt.{ConnectPacket, ConnectPacketVariableHeader, MQTTPacket, QualityOfService}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

/**
  * Created by nico on 17/02/2016.
  */
object MQTTClient with LazyLogging {
  def apply()(implicit system: ActorSystem): MQTTClient = new MQTTClient(system)
}

case class MQTTURI(hostname: String, port: Int, protocol: String, secured:Boolean, userName: Option[String], password: Option[String])

final class MQTTClient(s: ActorSystem) extends LazyLogging {
  implicit private val system = s
  implicit private val materializer = ActorMaterializer()

  def connect(brokerUri: URI,
              clientId:Option[String]=None,
              cleanSession: Option[Boolean]=None,
              keepAlive: Option[Int]=None,
              willTopic: Option[String]=None,
              willMessage: Option[String]=None) = {
    val mqttURI = decodeURI(brokerUri)
  }

  private def createFlow(mQTTURI: MQTTURI): Flow[MQTTPacket, MQTTPacket] = {
    Tcp().outgoingConnection(mQTTURI.hostname, mQTTURI.port)
      .join(StatFlow.bytesThroughCounterFlow())
  }

  private[client] def decodeURI(uri: URI): Try[MQTTURI] = {
    val protocols = List("mqtt", "ws")
    val protocols_secured = List("mqtts", "wss")
    if (!(protocols ::: protocols_secured).contains(uri.getScheme))
      Failure(new MalformedURLException(s"Invalid protocol '${uri.getScheme()}' in URI: $uri"))
    else if (uri.getHost == null || uri.getHost.trim.isEmpty) {
      Failure(new MalformedURLException(s"Host is undefined in URI: $uri"))
    }
    else {
      val protocol = if(uri.getScheme.startsWith("ws")) "ws" else "mqtt"
      val secured = if(protocols_secured.contains(uri.getScheme)) true else false
      val (username, password) = uri.getUserInfo match {
        case null | "" => (None, None)
        case userInfo:String => userInfo.split(":") match {
          case Array(user, password) => (Some(user), Some(password))
          case Array(user) => (Some(user), None)
        }
      }
      val port = uri.getPort match {
        case p if p > -1 => p
        case _ => uri.getScheme match {
          case "mqtt" => system.settings.config.getInt("casquette.default.tcp-port.mqtt")
          case "mqtts" => system.settings.config.getInt("casquette.default.tcp-port.mqtts")
          case "ws" => system.settings.config.getInt("casquette.default.tcp-port.ws")
          case "wss" => system.settings.config.getInt("casquette.default.tcp-port.wss")
        }
      }
      Success(MQTTURI(uri.getHost, port, protocol, secured, username, password))
    }
  }

  def test(): Unit = {
    logger.debug("Connecting to test.mosquitto.org:1883")
    val variableHeader = new ConnectPacketVariableHeader(
      userNameFlag=false,
      passwordFlag=false,
      willRetainFlag=false,
      willQos=QualityOfService.QOS_0,
      willFlag=false,
      cleanSessionFlag=false,
      keepAlive=0
    )

    val packet = new ConnectPacket(variableHeader, "", None, None, None, None)
    val con = Tcp().outgoingConnection("test.mosquitto.org", 1883)
    val flow = con.join(CodecFlow.codecFlow)
    val result = Source(List(packet)).via(flow).runWith(Sink.foreach(println))
    println(Await.result(result, Duration(2, TimeUnit.SECONDS)))
  }
}
