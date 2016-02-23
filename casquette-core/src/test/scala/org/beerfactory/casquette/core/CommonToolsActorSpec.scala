package org.beerfactory.casquette.core

import java.util.UUID

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask

/**
  * Created by nico on 19/02/2016.
  */
class CommonToolsActorSpec extends Specification {
  "CommonTools actor" should {
    implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())
    implicit val timeout = Timeout(10 seconds)
    val actorRef = TestActorRef(new CommonToolsActor())

    "generate UUIDs" in {
      val future = actorRef ? CommonToolsActor.GetUUID
      val result = Await.result(future, timeout.duration).asInstanceOf[UUID]
      system.terminate()
      result must beAnInstanceOf[UUID]
    }
    "generate valid client ID" in {
      val base = "casquette"
      val future = actorRef ? CommonToolsActor.RandomClientID
      val result = Await.result(future, timeout.duration).asInstanceOf[String]
      result must contain(base + "/")
      result.length mustEqual(23)
    }
  }
}
