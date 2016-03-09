package org.beerfactory.casquette.core.stats

import akka.stream.FanOutShape.{Name, Init}
import akka.stream.{FanOutShape, Outlet, Inlet, FanOutShape2}
import akka.util.ByteString

/**
  * Created by nico on 08/03/2016.
  */
class StatShape(_init: Init[ByteString] = Name("StatShape")) extends FanOutShape2[ByteString, ByteString, Stat](_init) {
  def this(in: Inlet[ByteString], out0: Outlet[ByteString], out1: Outlet[Stat]) = this(FanOutShape.Ports(in, out0 :: out1 :: Nil))
  protected override def construct(i: Init[ByteString]) = new StatShape(i)
}
