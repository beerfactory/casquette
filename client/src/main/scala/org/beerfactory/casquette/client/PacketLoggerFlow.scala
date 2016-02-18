package org.beerfactory.casquette.client

import akka.stream.scaladsl.BidiFlow
import com.typesafe.scalalogging.StrictLogging
import org.beerfactory.casquette.mqtt.MQTTPacket

/**
  * Created by njouanin on 18/02/16.
  */
object PacketLoggerFlow extends StrictLogging {
  def log(prefix: String, loggerMethod: (String) => Unit)(packet: MQTTPacket) = {
    loggerMethod(prefix + packet.toString)
    packet
  }

  def loggerFlow(loggerMethod: (String) => Unit): Unit = {
    BidiFlow.fromFunctions(log("--> ", loggerMethod), log("<-- ", loggerMethod))
  }
}
