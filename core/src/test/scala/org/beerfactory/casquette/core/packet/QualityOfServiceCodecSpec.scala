package org.beerfactory.casquette.core.packet

import org.specs2.mutable.Specification
import scodec.Attempt
import scodec.bits._
import org.beerfactory.casquette.core.SpecUtils._

/**
  * Created by nico on 14/02/2016.
  */
class QualityOfServiceCodecSpec extends Specification {
  "QualityOfServiceCodecSpec encode" should {
    "succeed with result 0x00 for QOS_1" in {
      //val codec = new QualityOfServiceCodec
      //codec.encode(QualityOfService.QOS_0) must beEqualTo(Attempt.successful(bin"00"))
      QualityOfService.QOS_0 must beEncodedAs(bin"00").withCodec(new QualityOfServiceCodec())
    }

  }
/*
  it should "succeed with result 0x01 for QOS_1" in {
    val codec = new QualityOfServiceCodec
    assert(codec.encode(QualityOfService.QOS_1) == Attempt.successful(bin"01"))
  }

  it should "succeed with result 0x10 for QOS_2" in {
    val codec = new QualityOfServiceCodec
    assert(codec.encode(QualityOfService.QOS_2) == Attempt.successful(bin"10"))
  }

  "QualityOfServiceCodecSpec decode" should "succeed with result QOS_0 for binary 00" in {
    val codec = new QualityOfServiceCodec
    assert(codec.decode(bin"00").require.value == QualityOfService.QOS_0)
  }

  it should "succeed with result QOS_1 for binary 01" in {
    val codec = new QualityOfServiceCodec
    assert(codec.decode(bin"01").require.value == QualityOfService.QOS_1)
  }

  it should "succeed with result QOS_2 for binary 10" in {
    val codec = new QualityOfServiceCodec
    assert(codec.decode(bin"10").require.value == QualityOfService.QOS_2)
  }

  it should "fail for any other value" in {
    val codec = new QualityOfServiceCodec
    assert(codec.decode(bin"11").isFailure)
  }
*/
}
