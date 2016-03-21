package org.beerfactory.casquette.core

import java.util.UUID

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.beerfactory.casquette.core.StatActor.GetStats
import org.beerfactory.casquette.core.stream.stats.BytesInStat
import org.specs2.mutable.Specification
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Created by nico on 20/03/2016.
  */
class StatActorSpec extends Specification {
  "StatActor actor" should {
    implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())
    implicit val timeout = Timeout(10 seconds)

    "[1] record BytesInStat" in {
      val actorRef = TestActorRef(new StatActor())
      actorRef ! BytesInStat(1)
      val result = Await.result(actorRef ? GetStats, timeout.duration).asInstanceOf[Stat]
      result.bytesIn must beEqualTo(1)
    }
    "[2] record BytesInStat" in {
      val actorRef = TestActorRef(new StatActor())
      actorRef ! BytesInStat(1)
      actorRef ! BytesInStat(1)
      val result = Await.result(actorRef ? GetStats, timeout.duration).asInstanceOf[Stat]
      result.bytesIn must beEqualTo(2)
    }
  }
}
