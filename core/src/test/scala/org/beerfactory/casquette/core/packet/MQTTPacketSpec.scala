package org.beerfactory.casquette.core.packet

import org.scalatest.FlatSpec
import scodec.{DecodeResult, Codec}
import scodec.bits._
import scodec.codecs._

/**
  * Created by nico on 12/02/2016.
  */
class MQTTPacketSpec extends FlatSpec {
  "A connect packet" should "be encoded as valid" in {
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
    assert(Codec[MQTTPacket].decode(encoded) == DecodeResult(packet, bin""))
  }
}
