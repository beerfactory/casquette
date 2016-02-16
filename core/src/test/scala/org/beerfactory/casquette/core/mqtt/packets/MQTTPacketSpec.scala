package org.beerfactory.casquette.core.mqtt.packets

import org.beerfactory.casquette.core.mqtt.QualityOfService
import org.specs2.mutable.Specification
import scodec.{Err, DecodeResult, Codec}
import scodec.bits._
import scodec.codecs._
import org.beerfactory.casquette.core.SpecUtils._

/**
  * Created by nico on 12/02/2016.
  */
class MQTTPacketSpec extends Specification {
  "A CONNECT packet" should {
    "[0] be successfully encoded/decoded" in {
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
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
    "[1] be successfully encoded/decoded" in {
      val variableHeader = new ConnectPacketVariableHeader(
        userNameFlag=true,
        passwordFlag=true,
        willRetainFlag=true,
        willQos=QualityOfService.QOS_2,
        willFlag=true,
        cleanSessionFlag=true,
        keepAlive=0
      )
      val packet = new ConnectPacket(variableHeader,
        "someClientId",
        Some("willTopic"),
        Some("willMessage"),
        Some("username"),
        Some("password"))
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
  }
  "A CONNACK packet" should {
    "[0] be successfully encoded/decoded" in {
      val packet = new ConnackPacket(false, 0)
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
    "[1] be successfully encoded/decoded" in {
      val packet = new ConnackPacket(true, 2)
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
  }
  "A PUBLISH packet" should {
    "[0] be successfully encoded/decoded" in {
      val packet = new PublishPacket(new PublishPacketFixedHeader(false, QualityOfService.QOS_0, false), "a/b", None, ByteVector.empty)
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
  }
  "[1] be successfully encoded/decoded" in {
    val packet = new PublishPacket(new PublishPacketFixedHeader(false, QualityOfService.QOS_1, false), "a/b", Some(1), ByteVector.empty)
    val encoded = Codec[MQTTPacket].encode(packet).require
    Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
  }
}
