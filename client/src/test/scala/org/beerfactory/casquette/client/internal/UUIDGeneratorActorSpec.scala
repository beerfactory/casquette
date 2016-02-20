package org.beerfactory.casquette.client.internal

import java.util.UUID

import akka.testkit.TestActorRef
import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.ask

/**
  * Created by nico on 19/02/2016.
  */
class UUIDGeneratorActorSpec extends Specification {
  "A UUIDGeneratorActorSpec" should {
    "Generate UUIDs" in {
      implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())
      implicit val timeout = Timeout(10 seconds)
      val actorRef = TestActorRef(new UUIDGeneratorActor())
      val future = actorRef ? UUIDGeneratorActor.GetUUID
      val result = Await.result(future, timeout.duration).asInstanceOf[UUID]
      system.terminate()
      result must beAnInstanceOf[UUID]
    }
  }
}
