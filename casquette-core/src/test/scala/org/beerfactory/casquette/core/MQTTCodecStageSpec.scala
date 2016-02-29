package org.beerfactory.casquette.core

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{BidiFlow, Flow, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import org.beerfactory.casquette.mqtt.MQTTPacket
import org.specs2.mutable.Specification
import scodec.bits.ByteVector
import scala.concurrent.duration._


/**
  * Created by nico on 29/02/2016.
  */
class MQTTCodecStageSpec extends Specification {
  "MQTT frame parser" should {
    implicit val system = ActorSystem("test-system", ConfigFactory.load())
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(10 seconds)

    "[1] Parse MQTT packet from basic byte stream" in {
      val packetBytes = Array[Byte](0x00, 0x01, 0x00)
      val sourceUnderTest = Source(List(ByteString(packetBytes))).via(BidiFlow.fromGraph(new MQTTCodecStage).join(Flow[MQTTPacket].map(x => x)))
      sourceUnderTest.runWith(TestSink.probe[ByteString])
        .request(1)
        .expectNext(ByteString(packetBytes))
        .expectComplete() must not(throwA[AssertionError])
    }
  }
}
