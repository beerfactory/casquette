package org.beerfactory.casquette.core

import scodec.codecs._

import scala.annotation.switch

package object packet {
  /*
  implicit class BoolToInt(val b:Boolean) extends AnyVal {
    def toInt = if (b) 1 else 0
    def * (x:Int) = if (b) x else 0
  }
  */

  object QualityOfService extends Enumeration {
    type QualityOfService = Value
    val QOS_0 = Value(0)
    val QOS_1 = Value(1)
    val QOS_2 = Value(2)

    def fromInt(qos: Int): QualityOfService =
      (qos: @switch) match {
        case 0 ⇒ QOS_0
        case 1 ⇒ QOS_1
        case 2 ⇒ QOS_2
        case _ ⇒ throw new IllegalArgumentException("Quality of service should be in the range [0..2]")
      }
  }
}
