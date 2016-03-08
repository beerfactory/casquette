package org.beerfactory.casquette.core.stats

import akka.stream.Graph
import akka.stream.scaladsl.{Broadcast, GraphDSL}
import akka.util.ByteString

/**
  * Created by nico on 08/03/2016.
  */
sealed trait Stat
case class ByteStat(size: Int) extends Stat

object StatFlow {
  def bytesThroughStat = {
    GraphDSL.create() { b =>
      import GraphDSL.Implicits._

      val broadcast = b.add(Broadcast[ByteString](2))
      StatShape(broadcast.in, broadcast.out(0), broadcast.out(1))
    }
  }
}
