package org.beerfactory.casquette.core.stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.testkit.scaladsl.TestSink
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import org.beerfactory.casquette.mqtt._
import org.specs2.mutable.Specification
import scodec.bits.ByteVector

import scala.concurrent.duration._



/**
  * Created by nico on 29/02/2016.
  */
class MQTTCodecStageSpec extends Specification {
  implicit val system = ActorSystem("test-system", ConfigFactory.load())
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10 seconds)

  "MQTT Codec stage" should {
    "roundtrip CONNECT message" in {
      val variableHeader = new ConnectPacketVariableHeader(
        userNameFlag=true,
        passwordFlag=true,
        willRetainFlag=false,
        willQos=QualityOfService.QOS_1,
        willFlag=true,
        cleanSessionFlag=true,
        keepAlive=0
      )
      val message = new ConnectPacket(variableHeader,
        "0123456789",
        Some("WillTopic"),
        Some("WillMessage"),
        Some("user"),
        Some("password"))
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip CONNACK message" in {
      val message = new ConnAckPacket(false, 0)
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip PUBLISH message" in {
      val message = new PublishPacket(new PublishPacketFixedHeader(false, QualityOfService.QOS_0, false), "a/b", None, ByteVector.empty)
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip PUBACK message" in {
      val message = new PubAckPacket(1)
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip PUBREC message" in {
      val message = new PubRecPacket(1)
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip PUBREL message" in {
      val message = new PubRelPacket(1)
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip PUBCOMP message" in {
      val message = new PubCompPacket(1)
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip SUBSCRIBE message" in {
      val message = new SubscribePacket(1, Vector(("a/b", QualityOfService.QOS_0), ("c/d", QualityOfService.QOS_1), ("e/f", QualityOfService.QOS_2)))
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip SUBACK message" in {
      val message = new SubAckPacket(1, Vector(0x00.toByte, 0x01.toByte, 0x02.toByte, 0x80.toByte))
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip UNSUBSCRIBE message" in {
      val message = new UnSubscribePacket(1, Vector("a/b", "c/d", "e/f"))
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip UNSUBACK message" in {
      val message = new UnSubAckPacket(1)
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip PINGREQ message" in {
      val message = new PingReqPacket()
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip PINGRESP message" in {
      val message = new PingRespPacket()
      roundtrip(message) must not(throwA[AssertionError])
    }
    "roundtrip DISCONNECT message" in {
      val message = new DisconnectPacket()
      roundtrip(message) must not(throwA[AssertionError])
    }

  }

  def roundtrip(message: MQTTPacket) = {
    val sourceUnderTest = Source.single(message).via(MQTTCodecStage().reversed.join(Flow[ByteString].map(x => x)))
    sourceUnderTest.runWith(TestSink.probe[MQTTPacket])
      .request(1)
      .expectNext(message)
      .expectComplete()

  }
}
