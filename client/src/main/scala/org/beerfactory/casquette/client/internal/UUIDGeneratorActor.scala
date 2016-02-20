package org.beerfactory.casquette.client.internal

import java.util.UUID

import akka.actor.{Props, ActorLogging, Actor}
import org.beerfactory.casquette.client.internal.UUIDGeneratorActor.GetUUID

import scala.collection.mutable


object UUIDGeneratorActor {
  def props(bufferSize: Int=1000): Props = Props(new UUIDGeneratorActor(bufferSize))
  case object GetUUID
}

private case object GenerateUUID

/**
  * Created by nico on 19/02/2016.
  */
class UUIDGeneratorActor(bufferSize: Int=1000) extends Actor with ActorLogging {
  val uuidQueue = new mutable.Queue[UUID]()
  self ! GenerateUUID

  def receive: Receive = {
    case GenerateUUID ⇒ for (i <- 1 to bufferSize) uuidQueue.enqueue(UUID.randomUUID())
    case GetUUID ⇒
      sender() ! uuidQueue.dequeue()
      if(uuidQueue.isEmpty)
        self ! GenerateUUID
  }
}
