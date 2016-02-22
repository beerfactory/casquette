package org.beerfactory.casquette.core

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import org.beerfactory.casquette.core.CommonToolsActor.{RandomClientID, GetUUID}

import scala.collection.mutable
import scala.util.Random


object CommonToolsActor {
  def props(bufferSize: Int=1000): Props = Props(new CommonToolsActor(bufferSize))
  case class RandomClientID(base:String)
  case object GetUUID
}

private case object GenerateUUID

/**
  * Created by nico on 19/02/2016.
  */
class CommonToolsActor(bufferSize: Int=1000) extends Actor with ActorLogging {
  val uuidQueue = new mutable.Queue[UUID]()
  self ! GenerateUUID

  def receive: Receive = {
    case GenerateUUID ⇒ for (i <- 1 to bufferSize) uuidQueue.enqueue(UUID.randomUUID())
    case GetUUID ⇒
      sender() ! uuidQueue.dequeue()
      if(uuidQueue.isEmpty)
        self ! GenerateUUID
    case RandomClientID(base) ⇒ {
        sender() ! base + "/" + Seq.fill(22-base.length)(Random.nextPrintableChar()).foldLeft(""){ (a,b) => a+ b}
    }
  }
}
