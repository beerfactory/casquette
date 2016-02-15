package org.beerfactory.casquette.core.packet

import org.specs2.mutable.Specification
import scodec.{Err, DecodeResult}
import scodec.bits._
import org.beerfactory.casquette.core.SpecUtils._

/**
  * Created by nico on 14/02/2016.
  */
class QualityOfServiceCodecSpec extends Specification {
  "QualityOfServiceCodecSpec codec" should {
    "succeed in encoding valid QOS" in {
      MQTTCodecs.qosCodec.encode(QualityOfService.QOS_0) must succeedWith(bin"00")
      MQTTCodecs.qosCodec.encode(QualityOfService.QOS_1) must succeedWith(bin"01")
      MQTTCodecs.qosCodec.encode(QualityOfService.QOS_2) must succeedWith(bin"10")
    }
    "succeed in decoding QOS bytes" in {
      MQTTCodecs.qosCodec.decode(bin"00") must succeedWith(DecodeResult(QualityOfService.QOS_0, BitVector.empty))
      MQTTCodecs.qosCodec.decode(bin"01") must succeedWith(DecodeResult(QualityOfService.QOS_1, BitVector.empty))
      MQTTCodecs.qosCodec.decode(bin"10") must succeedWith(DecodeResult(QualityOfService.QOS_2, BitVector.empty))
    }
    "succeed in decoding other QOS" in {
      MQTTCodecs.qosCodec.decode(bin"11") must failWith(Err(s"Invalid QOS value 11"))
    }
  }
}
