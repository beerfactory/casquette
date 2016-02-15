package org.beerfactory.casquette.core.mqtt.packets

import org.beerfactory.casquette.core.mqtt.QualityOfService
import org.specs2.mutable.Specification
import scodec.{DecodeResult, Codec}
import scodec.bits._
import scodec.codecs._
import org.beerfactory.casquette.core.SpecUtils._

/**
  * Created by nico on 12/02/2016.
  */
class MQTTPacketSpec extends Specification {
  "A connect packet" should {
    "[0] be successfully encoded/decoded" in {
      val fixedHeader = new ConnectPacketFixedHeader(dupFlag=false, qos=QualityOfService.QOS_0, retain=false)
      val variableHeader = new ConnectPacketVariableHeader(
        userNameFlag=false,
        passwordFlag=false,
        willRetainFlag=false,
        willQos=QualityOfService.QOS_0,
        willFlag=false,
        cleanSessionFlag=false,
        keepAlive=0
      )
      val packet = new ConnectPacket(fixedHeader, variableHeader, "", None, None, None, None)
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
    "[1] be successfully encoded/decoded" in {
      val fixedHeader = new ConnectPacketFixedHeader(dupFlag=false, qos=QualityOfService.QOS_0, retain=false)
      val variableHeader = new ConnectPacketVariableHeader(
        userNameFlag=true,
        passwordFlag=true,
        willRetainFlag=true,
        willQos=QualityOfService.QOS_2,
        willFlag=true,
        cleanSessionFlag=true,
        keepAlive=0
      )
      val packet = new ConnectPacket(fixedHeader,
        variableHeader,
        "someClientId",
        Some("willTopic"),
        Some("willMessage"),
        Some("username"),
        Some("password"))
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
  }
  "A connack packet" should {
    "[0] be successfully encoded/decoded" in {
      val packet = new ConnackPacket(new FixedHeader(false, false, false, false), false, 0)
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
    "[1] be successfully encoded/decoded" in {
      val packet = new ConnackPacket(new FixedHeader(false, false, false, false), true, 2)
      val encoded = Codec[MQTTPacket].encode(packet).require
      Codec[MQTTPacket].decode(encoded) must succeedWith(DecodeResult(packet, BitVector.empty))
    }
  }
}
