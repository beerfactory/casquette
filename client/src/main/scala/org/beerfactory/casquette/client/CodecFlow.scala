package org.beerfactory.casquette.client

import akka.NotUsed
import akka.stream.scaladsl.BidiFlow
import akka.util.ByteString
import org.beerfactory.casquette.mqtt.MQTTPacket
import scodec.Codec
import scodec.bits.{BitVector}

/**
  * Created by nico on 17/02/2016.
  */
object CodecFlow {
  def decode(data: ByteString): MQTTPacket = {
    println(data)
    Codec[MQTTPacket].decode(BitVector(data.asByteBuffer)).require.value
  }

  def encode(packet: MQTTPacket):ByteString = {
    val b = ByteString(Codec[MQTTPacket].encode(packet).require.toByteArray)
    println(b)
    b
  }

  def codecFlow:BidiFlow[ByteString, MQTTPacket, MQTTPacket, ByteString, NotUsed] = BidiFlow.fromFunctions(decode, encode)
}
