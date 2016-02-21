package org.beerfactory.casquette.client

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source, Tcp}
import akka.util.ByteString
import com.typesafe.config.{ConfigFactory, Config}
import com.typesafe.scalalogging.Logger
import org.beerfactory.casquette.mqtt.{PingReqPacket, ConnectPacket, QualityOfService, ConnectPacketVariableHeader}
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by nico on 17/02/2016.
  */
class MQTTClient(configOption:Option[Config]) {
  val logger = Logger(LoggerFactory.getLogger(classOf[MQTTClient]))

  private val config = configOption match {
    case Some(c:Config) => c
    case _ => ConfigFactory.load()
  }

  implicit private val system = ActorSystem("casquette-client", config)
  implicit private val materializer = ActorMaterializer()

  val variableHeader = new ConnectPacketVariableHeader(
    userNameFlag=false,
    passwordFlag=false,
    willRetainFlag=false,
    willQos=QualityOfService.QOS_0,
    willFlag=false,
    cleanSessionFlag=false,
    keepAlive=0
  )
  logger.debug("Connecting to test.mosquitto.org:1883")
  val packet = new ConnectPacket(variableHeader, "", None, None, None, None)
  val con = Tcp().outgoingConnection("test.mosquitto.org", 1883)
  val flow = con.join(CodecFlow.codecFlow)
  val result = Source(List(packet)).via(flow).runWith(Sink.foreach(println))
  println(Await.result(result, Duration(2, TimeUnit.SECONDS)))
  //println(result)

  def shutdown(): Unit = system.terminate()
}
