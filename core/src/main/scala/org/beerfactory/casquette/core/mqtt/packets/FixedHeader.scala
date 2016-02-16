package org.beerfactory.casquette.core.mqtt.packets

import org.beerfactory.casquette.core.mqtt.QualityOfService._
import org.beerfactory.casquette.core.mqtt.codecs.MQTTCodecs._
import scodec.codecs._
import scodec.bits._

sealed trait MQTTFixedHeader

object DefaultFixedHeader {
  implicit val codec = constant(bin"0000")
}

case class PublishPacketFixedHeader(dupFlag: Boolean, qos: QualityOfService, retain: Boolean) extends  MQTTFixedHeader

object PublishPacketFixedHeader {
  implicit val codec = (bool :: qosCodec :: bool).as[PublishPacketFixedHeader]
}

