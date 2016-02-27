package org.beerfactory.casquette.client

import java.net.URI

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask

import scala.util.Success

/**
  * Created by nico on 22/02/2016.
  */
class MQTTClientActorSpec extends Specification {
  "getHostPort method" should {
    implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())
    implicit val timeout = Timeout(10 seconds)
    val actorRef = TestActorRef(new MQTTClientActor())

    "return ('localhost', 1883) from \"mqtt://localhost\"" in {
      val actor = actorRef.underlyingActor
      actor.getHostPort(new URI("mqtt://localhost")) mustEqual Success(("localhost", 1883))
    }

    "return ('localhost', 8883) from \"mqtts://localhost\"" in {
      val actor = actorRef.underlyingActor
      actor.getHostPort(new URI("mqtts://localhost")) mustEqual Success(("localhost", 8883))
    }

    "return ('localhost', 80) from \"ws://localhost\"" in {
      val actor = actorRef.underlyingActor
      actor.getHostPort(new URI("ws://localhost")) mustEqual Success(("localhost", 80))
    }

    "return ('localhost', 443) from \"wss://localhost\"" in {
      val actor = actorRef.underlyingActor
      actor.getHostPort(new URI("wss://localhost")) mustEqual Success(("localhost", 443))
    }

    "return ('localhost', 1884) from \"mqtt://localhost:1884\"" in {
      val actor = actorRef.underlyingActor
      actor.getHostPort(new URI("mqtt://localhost:1884")) mustEqual Success(("localhost", 1884))
    }

    "fail with incorrect scheme \"mttq://localhost\"" in {
      val actor = actorRef.underlyingActor
      actor.getHostPort(new URI("mttq://localhost")) must beFailedTry
    }
  }

}
