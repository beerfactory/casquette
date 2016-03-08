package org.beerfactory.casquette.core.stats

import akka.stream.FanOutShape.{Name, Init}
import akka.stream.FanOutShape2
import akka.util.ByteString

/**
  * Created by nico on 08/03/2016.
  */
class StatShape(_init: Init[ByteString] = Name("StatShape"))
  extends FanOutShape2[ByteString, ByteString, Stat](_init) {

  protected override def construct(i: Init[ByteString]) = new StatShape(i)

  val bytesOut = newOutlet[ByteString]("bytesOut")
  val statsOut = newOutlet[Stat]("statsOut")
}
