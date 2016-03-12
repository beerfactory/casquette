package org.beerfactory.casquette.core.stats

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.{TestProbe, TestActors}
import akka.util.{Timeout, ByteString}
import com.typesafe.config.ConfigFactory
import org.beerfactory.casquette.mqtt.{PacketType, MQTTPacket, PingReqPacket}
import org.specs2.mutable.Specification
import scala.concurrent.duration._

/**
  * Created by nico on 10/03/2016.
  */
class StatFlowSpec extends Specification {
  implicit val system = ActorSystem("test-system", ConfigFactory.load())
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10 seconds)

  "Bytes through counter" should {
    "Generate 2 bytes stat " in {
      val probe = TestProbe()
      val elem = ByteString("ABCD")
      val sourceUnderTest = Source.single(elem).via(StatFlow.bytesThroughCounterFlow(probe.ref).join(Flow[ByteString].map(x => x)))
      sourceUnderTest.runWith(TestSink.probe[ByteString])
        .request(1)
        .expectNext(elem)
        .expectComplete() must not(throwA[AssertionError])
      probe.expectMsg(BytesInStat(elem.length)) must not(throwA[AssertionError])
      probe.expectMsg(BytesOutStat(elem.length)) must not(throwA[AssertionError])
    }
  }

  "Packet through counter" should {
    "Generate 2 packet stat " in {
      val probe = TestProbe()
      val elem = PingReqPacket()
      val sourceUnderTest = Source.single(elem).via(StatFlow.packetThroughCounterFlow(probe.ref).join(Flow[MQTTPacket].map(x => x)))
      sourceUnderTest.runWith(TestSink.probe[MQTTPacket])
        .request(1)
        .expectNext(elem)
        .expectComplete() must not(throwA[AssertionError])
      probe.expectMsg(PacketInStat(PacketType.PINGREQ)) must not(throwA[AssertionError])
      probe.expectMsg(PacketOutStat(PacketType.PINGREQ)) must not(throwA[AssertionError])
    }
  }
}
