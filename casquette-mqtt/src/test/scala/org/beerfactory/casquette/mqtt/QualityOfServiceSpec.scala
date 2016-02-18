package org.beerfactory.casquette.mqtt

import org.specs2.mutable.Specification

/**
  * Created by nico on 07/02/2016.
  */
class QualityOfServiceSpec extends Specification {

  "QualityOfService fromInt" should {
    "return QOS_0 for integer 0" in {
      QualityOfService.fromInt(0) must beEqualTo(QualityOfService.QOS_0)
    }
    "return QOS_1 for integer 1" in {
      QualityOfService.fromInt(1) must beEqualTo(QualityOfService.QOS_1)
    }
    "return QOS_2 for integer 2" in {
      QualityOfService.fromInt(2) must beEqualTo(QualityOfService.QOS_2)
    }
    "throw IllegalArgumentException otherwise" in {
      QualityOfService.fromInt(3) must throwA[IllegalArgumentException]
    }
  }
}
