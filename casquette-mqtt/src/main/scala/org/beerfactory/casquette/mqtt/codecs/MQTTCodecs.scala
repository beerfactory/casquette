package org.beerfactory.casquette.mqtt.codecs

import scodec.codecs._

object MQTTCodecs {
  val remainingLengthCodec = new RemainingLengthCodec
  val stringCodec = variableSizeBytes(uint16, utf8)
  val qosCodec = new QualityOfServiceCodec
  val packetIdCodec = short16
}
