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
class MQTTClientSpec extends Specification {
  "MQTTClient decodeUri" should {
    implicit val system = ActorSystem("TestActorSystem", ConfigFactory.load())

    "successfully decode \"mqtt://localhost\"" in {
      MQTTClient().decodeURI(new URI("mqtt://localhost")) mustEqual
        Success(MQTTURI("localhost", 1883, "mqtt", false, None, None))
    }

    "successfully decode \"mqtts://localhost\"" in {
      MQTTClient().decodeURI(new URI("mqtts://localhost")) mustEqual
        Success(MQTTURI("localhost", 8883, "mqtt", true, None, None))
    }

    "successfully decode \"ws://localhost\"" in {
      MQTTClient().decodeURI(new URI("ws://localhost")) mustEqual
        Success(MQTTURI("localhost", 80, "ws", false, None, None))
    }

    "successfully decode \"wss://localhost\"" in {
      MQTTClient().decodeURI(new URI("wss://localhost")) mustEqual
        Success(MQTTURI("localhost", 443, "ws", true, None, None))
    }

    "successfully decode \"mqtt://localhost:1884\"" in {
      MQTTClient().decodeURI(new URI("mqtt://localhost:1884")) mustEqual
        Success(MQTTURI("localhost", 1884, "mqtt", false, None, None))
    }

    "successfully decode \"mqtt://user:password@localhost\"" in {
      MQTTClient().decodeURI(new URI("mqtt://user:password@localhost")) mustEqual
        Success(MQTTURI("localhost", 1883, "mqtt", false, Some("user"), Some("password")))
    }

    "successfully decode \"mqtt://user@localhost\"" in {
      MQTTClient().decodeURI(new URI("mqtt://user@localhost")) mustEqual
        Success(MQTTURI("localhost", 1883, "mqtt", false, Some("user"), None))
    }

    "fail with incorrect scheme \"mttq://localhost\"" in {
      MQTTClient().decodeURI(new URI("mttq://localhost")) must beFailedTry
    }
  }

}
