package org.beerfactory.casquette.core

import akka.actor.{Actor, ActorLogging}
import org.beerfactory.casquette.core.StatActor.{GetStats, ReInit}
import org.beerfactory.casquette.core.stream.stats.{BytesInStat, BytesOutStat, PacketInStat, PacketOutStat}
import org.beerfactory.casquette.mqtt.PacketType.PacketType

/**
  * Created by nico on 19/03/2016.
  */
object StatActor {
  object ReInit
  object GetStats
}

case class Stat(bytesIn: Long, bytesOut: Long, packetsIn: Map[PacketType, Int], packetsOut: Map[PacketType, Int])

class StatActor(init: Option[Stat]=None) extends Actor with ActorLogging {
  var stats = init match {
    case None => newStat()
    case s:Some[Stat] => s.get
  }

  def receive: Receive = {
    case bytesIn:BytesInStat => stats = stats.copy(bytesIn = stats.bytesIn + bytesIn.size.toLong)
    case bytesOut:BytesOutStat => stats = stats.copy(bytesOut = stats.bytesOut + bytesOut.size.toLong)
    case packetIn:PacketInStat => stats = stats.copy(packetsIn=stats.packetsIn.updated(packetIn.packetType, stats.packetsIn(packetIn.packetType) + 1))
    case packetOut:PacketOutStat => stats = stats.copy(packetsOut=stats.packetsOut.updated(packetOut.packetType, stats.packetsOut(packetOut.packetType) + 1))
    case ReInit => stats = newStat()
    case GetStats => sender() ! stats
  }

  private def newStat(): Stat = new Stat(0L, 0L, Map[PacketType, Int]().withDefaultValue(0), Map[PacketType, Int]().withDefaultValue(0))
}
