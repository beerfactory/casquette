package org.beerfactory.casquette.core.mqtt.packets

import org.beerfactory.casquette.core.mqtt.QualityOfService._
import org.beerfactory.casquette.core.mqtt.codecs.MQTTCodecs._
import scodec.codecs._

sealed trait MQTTFixedHeader

case class FixedHeader(flag_0: Boolean, flag_1: Boolean, flag_2: Boolean, flag_3: Boolean) extends MQTTFixedHeader

object FixedHeader {
  implicit val codec = (bool :: bool :: bool :: bool).as[FixedHeader]
}

case class PublishPacketFixedHeader(dupFlag: Boolean, qos: QualityOfService, retain: Boolean) extends  MQTTFixedHeader

object ConnectPacketFixedHeader {
  implicit val codec = (bool :: qosCodec :: bool).as[PublishPacketFixedHeader]
}

