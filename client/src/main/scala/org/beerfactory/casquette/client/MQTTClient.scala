package org.beerfactory.casquette.client

import java.net.URI
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source, Tcp}
import com.typesafe.scalalogging.LazyLogging
import org.beerfactory.casquette.mqtt.{ConnectPacket, ConnectPacketVariableHeader, QualityOfService}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by nico on 17/02/2016.
  */
object MQTTClient {
  def apply(implicit system: ActorSystem): MQTTClient = new MQTTClient()
}

final class MQTTClient(implicit system: ActorSystem) extends LazyLogging {
  implicit private val materializer = ActorMaterializer()

  def connect(brokerUri: URI) = ???
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
