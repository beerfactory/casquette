package org.beerfactory.casquette.mqtt

import scala.annotation.switch

/**
  * Created by nico on 10/03/2016.
  */
object PacketType extends Enumeration {
  type PacketType = Value
  val Reserved_0 = Value(0.toByte)
  val CONNECT = Value(1.toByte)
  val CONNACK = Value(2.toByte)
  val PUBLISH = Value(3.toByte)
  val PUBACK = Value(4.toByte)
  val PUBREC = Value(5.toByte)
  val PUBREL = Value(6.toByte)
  val PUBCOMP = Value(7.toByte)
  val SUBSCRIBE = Value(8.toByte)
  val SUBACK = Value(9.toByte)
  val UNSUBSCRIBE = Value(10.toByte)
  val UNSUBACK = Value(11.toByte)
  val PINGREQ = Value(12.toByte)
  val PINGRESP = Value(13.toByte)
  val DISCONNECT = Value(14.toByte)
  val Reserved_15 = Value(15.toByte)

  def fromByte(t: Byte): PacketType =
    (t: @switch) match {
      case 0 ⇒ Reserved_0
      case 1 ⇒ CONNECT
      case 2 ⇒ CONNACK
      case 3 ⇒ PUBLISH
      case 4 ⇒ PUBACK
      case 5 ⇒ PUBREC
      case 6 ⇒ PUBREL
      case 7 ⇒ PUBCOMP
      case 8 ⇒ SUBSCRIBE
      case 9 ⇒ SUBACK
      case 10 ⇒ UNSUBSCRIBE
      case 11 ⇒ UNSUBACK
      case 12 ⇒ PINGREQ
      case 13 ⇒ PINGRESP
      case 14 ⇒ DISCONNECT
      case 15 ⇒ Reserved_15
      case _ ⇒ throw new IllegalArgumentException("MQTT Packet type should be in the range [0..15]")
    }

  def fromPacket(packet: MQTTPacket): PacketType =
    packet match {
      case _:ConnectPacket ⇒ CONNECT
      case _:ConnAckPacket ⇒ CONNACK
      case _:PublishPacket ⇒ PUBLISH
      case _:PubAckPacket ⇒ PUBACK
      case _:PubRecPacket ⇒ PUBREC
      case _:PubRelPacket ⇒ PUBREL
      case _:PubCompPacket ⇒ PUBCOMP
      case _:SubscribePacket ⇒ SUBSCRIBE
      case _:SubAckPacket ⇒ SUBACK
      case _:UnSubscribePacket ⇒ UNSUBSCRIBE
      case _:UnSubAckPacket ⇒ UNSUBACK
      case _:PingReqPacket ⇒ PINGREQ
      case _:PingRespPacket ⇒ PINGRESP
      case _:DisconnectPacket ⇒ DISCONNECT
      case _ ⇒ throw new IllegalArgumentException("Invalid MQTT packet class")
    }
}