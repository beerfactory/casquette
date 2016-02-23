package org.beerfactory.casquette.core

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.Logging
import org.beerfactory.casquette.core.CommonToolsActor.{RandomClientID, GetUUID}

import scala.collection.mutable
import scala.util.Random


object CommonToolsActor {
  def props(bufferSize: Int=1000): Props = Props(new CommonToolsActor(bufferSize))
  case object RandomClientID
  case object GetUUID
}

private case object GenerateUUID
private case object GenerateClientID

/**
  * Created by nico on 19/02/2016.
  */
class CommonToolsActor(bufferSize: Int=1000) extends Actor with ActorLogging {
  final val clienIdBase = "casquette/"
  val uuidQueue = new mutable.Queue[UUID]()
  val randomIdQueue = new mutable.Queue[String]()

  self ! GenerateUUID
  self ! GenerateClientID

  def receive: Receive = {
    case GenerateUUID ⇒
      log.debug(s"Generating $bufferSize new UUIDs")
      for (i <- 1 to bufferSize) uuidQueue.enqueue(UUID.randomUUID())

    case GenerateClientID ⇒
      log.debug(s"Generating $bufferSize new client IDs")
      for (i <- 1 to bufferSize)
        randomIdQueue.enqueue(clienIdBase + Seq.fill(23-clienIdBase.length)(Random.nextPrintableChar()).mkString)

    case GetUUID ⇒
      sender() ! uuidQueue.dequeue()
      log.debug(s"UUID queue size=$uuidQueue.size")
      if(uuidQueue.isEmpty)
        self ! GenerateUUID

    case RandomClientID ⇒ {
      sender() ! randomIdQueue.dequeue()
      log.debug(s"ClientID queue size=$randomIdQueue.size")
      if(randomIdQueue.isEmpty)
        self ! GenerateClientID
    }
  }
}
