package org.beerfactory.casquette.mqtt

import scala.annotation.switch

/**
  * Created by nico on 18/02/2016.
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
