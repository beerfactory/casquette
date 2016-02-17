package org.beerfactory.casquette.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{ConfigFactory, Config}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

/**
  * Created by nico on 17/02/2016.
  */
class MQTTClient(configOption:Option[Config]) {
  val logger = Logger(LoggerFactory.getLogger(classOf[MQTTClient]))

  private val config = configOption match {
    case Some(c:Config) => c
    case _ => ConfigFactory.load()
  }

  implicit private val system = ActorSystem("casquette-client", config)
  implicit private val materializer = ActorMaterializer()

  def shutdown(): Unit = system.terminate()
}
