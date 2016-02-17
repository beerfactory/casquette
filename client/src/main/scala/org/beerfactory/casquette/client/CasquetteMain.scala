package org.beerfactory.casquette.client

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

/**
  * Created by nico on 16/02/2016.
  */
object CasquetteMain extends App {
  val logger = Logger(LoggerFactory.getLogger("CasquetteMain"))
  logger.debug("Starting ...")

  private lazy val config = {
    val _config = ConfigFactory.load()
    val internalConfig = _config.getConfig("casquette.internal-config")

    _config
      .withoutPath("akka")
      .withFallback(internalConfig)
  }

  val client = new MQTTClient(Some(config))
  logger.debug("System started")

  client.shutdown()
  logger.debug("Stopped")
}
