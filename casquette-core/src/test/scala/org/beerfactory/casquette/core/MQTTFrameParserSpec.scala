package org.beerfactory.casquette.core

import akka.actor.ActorSystem
import akka.actor.Status.Failure
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification
import akka.util.Timeout
import scodec.bits.ByteVector
import scala.concurrent.duration._

import scala.concurrent.Await
import scala.util.Random

/**
  * Created by nico on 28/02/2016.
  */
class MQTTFrameParserSpec extends Specification {
  "MQTT frame parser" should {
    implicit val system = ActorSystem("test-system", ConfigFactory.load())
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(10 seconds)

    "[1] Parse MQTT packet from basic byte stream" in {
      val packetBytes = Array[Byte](0x00, 0x01, 0x00)
      val sourceUnderTest = Source(List(ByteString(packetBytes))).via(Flow[ByteString].via(new MQTTFrameParser))
      sourceUnderTest.runWith(TestSink.probe[ByteVector])
        .request(1)
        .expectNext(ByteVector(packetBytes))
        .expectComplete() must not(throwA[AssertionError])
    }

    "[2] Parse MQTT packet from basic byte stream" in {
      val payload = Array.fill(128)(scala.util.Random.nextInt(256).toByte)
      val packetBytes = Array[Byte](0x00, 0x80.asInstanceOf[Byte], 0x01) ++ payload
      val sourceUnderTest = Source(List(ByteString(packetBytes))).via(Flow[ByteString].via(new MQTTFrameParser))
      sourceUnderTest.runWith(TestSink.probe[ByteVector])
        .request(1)
        .expectNext(ByteVector(packetBytes))
        .expectComplete() must not(throwA[AssertionError])
    }
    "Parse MQTT packet from cutted byte stream" in {
      val payload1 = Array.fill(64)(scala.util.Random.nextInt(256).toByte)
      val payload2 = Array.fill(64)(scala.util.Random.nextInt(256).toByte)
      val packetBytes = Array[Byte](0x00, 0x80.asInstanceOf[Byte], 0x01)
      val sourceUnderTest = Source(List(ByteString(packetBytes), ByteString(payload1), ByteString(payload2))).via(Flow[ByteString].via(new MQTTFrameParser))
      sourceUnderTest.runWith(TestSink.probe[ByteVector])
        .request(1)
        .expectNext(ByteVector(packetBytes ++ payload1 ++ payload2))
        .expectComplete() must not(throwA[AssertionError])
    }
    "Output successives MQTT packet from single byte stream" in {
      val packet1 = Array[Byte](0x00, 0x03) ++ Array.fill(3)(scala.util.Random.nextInt(256).toByte)
      val packet2 = Array[Byte](0x00, 0x08) ++ Array.fill(8)(scala.util.Random.nextInt(256).toByte)
      val sourceUnderTest = Source(List(ByteString(packet1 ++ packet2))).via(Flow[ByteString].via(new MQTTFrameParser))
      sourceUnderTest.runWith(TestSink.probe[ByteVector])
        .request(2)
        .expectNext(ByteVector(packet1), ByteVector(packet2))
        .expectComplete() must not(throwA[AssertionError])
    }

  }
}
