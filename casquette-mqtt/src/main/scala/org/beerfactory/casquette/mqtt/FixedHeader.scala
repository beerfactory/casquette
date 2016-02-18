package org.beerfactory.casquette.mqtt

import org.beerfactory.casquette.mqtt.QualityOfService.QualityOfService
import org.beerfactory.casquette.mqtt.codecs.MQTTCodecs._
import scodec.bits._
import scodec.codecs._

sealed trait MQTTFixedHeader

object DefaultFixedHeader {
  implicit val codec = constant(bin"0000")
}

case class PublishPacketFixedHeader(dupFlag: Boolean, qos: QualityOfService, retain: Boolean) extends  MQTTFixedHeader

object PublishPacketFixedHeader {
  implicit val codec = (bool :: qosCodec :: bool).as[PublishPacketFixedHeader]
}