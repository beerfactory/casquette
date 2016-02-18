package org.beerfactory.casquette.core.mqtt.packets

import akka.NotUsed
import akka.stream.scaladsl.BidiFlow
import scodec._
import scodec.bits.BitVector

/**
  * Created by nico on 17/02/2016.
  */
object CodecFlow {
  def decode(data: BitVector): MQTTPacket = Codec[MQTTPacket].decode(data).require.value

  def encode(packet: MQTTPacket):BitVector = Codec[MQTTPacket].encode(packet).require

  def codecFlow:BidiFlow[BitVector, MQTTPacket, MQTTPacket, BitVector, NotUsed] = BidiFlow.fromFunctions(decode, encode)
}
