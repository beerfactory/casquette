package org.beerfactory.casquette.client.internal

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{BidiFlow, Flow, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import org.beerfactory.casquette.mqtt.{MQTTPacket, PingReqPacket}
import org.specs2.mutable.Specification

import scala.concurrent.duration._

/**
  * Created by nico on 14/03/2016.
  */
class KeepAliveStageSpec extends Specification {
  implicit val system = ActorSystem("test-system", ConfigFactory.load())
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10 seconds)

  "KeepAlive stage" should {
    "send keep alive messages " in {
      val stage = BidiFlow.fromGraph(new KeepAliveStage(2 seconds))
      val sourceUnderTest = Source.empty.via(stage.join(Flow[MQTTPacket].map(x => x)))
      sourceUnderTest.runWith(TestSink.probe[MQTTPacket])
        .expectNext(3 seconds, PingReqPacket()) must not(throwA[AssertionError])
    }
  }
}
