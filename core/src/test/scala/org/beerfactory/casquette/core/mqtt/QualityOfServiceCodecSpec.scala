package org.beerfactory.casquette.core.mqtt

import org.beerfactory.casquette.core.SpecUtils._
import org.beerfactory.casquette.core.packet.QualityOfService
import org.beerfactory.casquette.core.mqtt.MQTTCodecs._
import org.specs2.mutable.Specification
import scodec.bits._
import scodec.{DecodeResult, Err}

/**
  * Created by nico on 14/02/2016.
  */
class QualityOfServiceCodecSpec extends Specification {
  "QualityOfServiceCodecSpec codec" should {
    "succeed in encoding valid QOS" in {
      qosCodec.encode(QualityOfService.QOS_0) must succeedWith(bin"00")
      qosCodec.encode(QualityOfService.QOS_1) must succeedWith(bin"01")
      qosCodec.encode(QualityOfService.QOS_2) must succeedWith(bin"10")
    }
    "succeed in decoding QOS bytes" in {
      qosCodec.decode(bin"00") must succeedWith(DecodeResult(QualityOfService.QOS_0, BitVector.empty))
      qosCodec.decode(bin"01") must succeedWith(DecodeResult(QualityOfService.QOS_1, BitVector.empty))
      qosCodec.decode(bin"10") must succeedWith(DecodeResult(QualityOfService.QOS_2, BitVector.empty))
    }
    "succeed in decoding other QOS" in {
      qosCodec.decode(bin"11") must failWith(Err(s"Invalid QOS value 11"))
    }
  }
}
