package org.beerfactory.casquette.core

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.beerfactory.casquette.mqtt.{ConnectPacket, QualityOfService, ConnectPacketVariableHeader, MQTTPacket}
import org.specs2.mutable.Specification
import akka.util.Timeout
import scodec.Codec
import scala.concurrent.duration._
import scodec.bits._

import scala.concurrent.Await
import scala.util.Random

/**
  * Created by nico on 28/02/2016.
  */
class MQTTDecodeStageSpec extends Specification {
  "MQTT frame parser" should {
    implicit val system = ActorSystem("test-system", ConfigFactory.load())
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(10 seconds)

    "Decode MQTT packet from basic byte stream" in {
      val sourceUnderTest = Source.single(ByteString(encoded(testPacket).toByteArray)).via(Flow[ByteString].via(new MQTTDecodeStage))
      sourceUnderTest.runWith(TestSink.probe[MQTTPacket])
        .request(1)
        .expectNext(testPacket)
        .expectComplete() must not(throwA[AssertionError])
    }

    "Decode MQTT packet from cutted byte stream" in {
      val cut = encoded(testPacket).length / 2
      val first = encoded(testPacket).take(cut)
      val second = encoded(testPacket).drop(cut)
      val sourceUnderTest = Source(List(ByteString(first.toByteArray), ByteString(second.toByteArray))).via(Flow[ByteString].via(new MQTTDecodeStage))
      sourceUnderTest.runWith(TestSink.probe[MQTTPacket])
        .request(1)
        .expectNext(testPacket)
        .expectComplete() must not(throwA[AssertionError])
    }
    "decode successives MQTT packets from single byte stream" in {
      val packet1, packet2 = encoded(testPacket)
      val sourceUnderTest = Source.single(ByteString((packet1 ++ packet2).toByteArray)).via(Flow[ByteString].via(new MQTTDecodeStage))
      sourceUnderTest.runWith(TestSink.probe[MQTTPacket])
        .request(2)
        .expectNext(testPacket, testPacket)
        .expectComplete() must not(throwA[AssertionError])
    }

  }

  def encoded(packet: MQTTPacket) = Codec[MQTTPacket].encode(packet).require

  def testPacket = {
    val variableHeader = new ConnectPacketVariableHeader(
      userNameFlag=true,
      passwordFlag=true,
      willRetainFlag=false,
      willQos=QualityOfService.QOS_1,
      willFlag=true,
      cleanSessionFlag=true,
      keepAlive=0
    )
    new ConnectPacket(variableHeader,
      "0123456789",
      Some("WillTopic"),
      Some("WillMessage"),
      Some("user"),
      Some("password"))
  }

}
