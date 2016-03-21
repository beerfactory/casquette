package org.beerfactory.casquette.core.stream.stats

import akka.actor.ActorRef
import akka.stream.scaladsl.BidiFlow
import akka.util.ByteString
import org.beerfactory.casquette.mqtt.PacketType.PacketType
import org.beerfactory.casquette.mqtt.{PacketType, MQTTPacket}
import org.beerfactory.casquette.mqtt.PacketType.PacketType


/**
  * Created by nico on 08/03/2016.
  */
sealed trait Stat
case class BytesInStat(size: Int) extends Stat
case class BytesOutStat(size: Int) extends Stat
case class PacketInStat(packetType: PacketType) extends Stat
case class PacketOutStat(packetType: PacketType) extends Stat


object StatFlow {
  private def bytesThroughStatFlow(statActor: ActorRef, topFlowStatBuilder:(ByteString) => Stat, bottomFlowStatBuilder:(ByteString) => Stat) = {
    def statBytes(statActor: ActorRef, statBuilder:(ByteString) => Stat)(data: ByteString) = {
      statActor ! statBuilder(data)
      data
    }
    BidiFlow.fromFunctions(
      statBytes(statActor, topFlowStatBuilder),
      statBytes(statActor, bottomFlowStatBuilder)
    )
  }
  private def packetThroughStatFlow(statActor: ActorRef, topFlowStatBuilder:(MQTTPacket) => Stat, bottomFlowStatBuilder:(MQTTPacket) => Stat) = {
    def statPacket(statActor: ActorRef, statBuilder:(MQTTPacket) => Stat)(packet: MQTTPacket) = {
      statActor ! statBuilder(packet)
      packet
    }
    BidiFlow.fromFunctions(
      statPacket(statActor, topFlowStatBuilder),
      statPacket(statActor, bottomFlowStatBuilder)
    )
  }

  def bytesThroughCounterFlow(statActor: ActorRef) = {
    bytesThroughStatFlow(statActor, data => BytesInStat(data.length), data => BytesOutStat(data.length))
  }
  def packetThroughCounterFlow(statActor: ActorRef) = {
    packetThroughStatFlow(statActor,
      packet => PacketInStat(PacketType.fromPacket(packet)),
      packet => PacketOutStat(PacketType.fromPacket(packet)))
  }

}
