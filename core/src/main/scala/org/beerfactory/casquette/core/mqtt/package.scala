package org.beerfactory.casquette.core

import scala.annotation.switch

package object mqtt {
  /*
  implicit class BoolToInt(val b:Boolean) extends AnyVal {
    def toInt = if (b) 1 else 0
    def * (x:Int) = if (b) x else 0
  }
  */

  object QualityOfService extends Enumeration {
    type QualityOfService = Value
    val QOS_0 = Value(0.toByte)
    val QOS_1 = Value(1.toByte)
    val QOS_2 = Value(2.toByte)

    def fromInt(qos: Byte): QualityOfService =
      (qos: @switch) match {
        case 0 ⇒ QOS_0
        case 1 ⇒ QOS_1
        case 2 ⇒ QOS_2
        case _ ⇒ throw new IllegalArgumentException("Quality of service should be in the range [0..2]")
      }
  }
}
