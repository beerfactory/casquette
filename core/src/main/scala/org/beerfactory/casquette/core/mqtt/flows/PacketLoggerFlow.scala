package org.beerfactory.casquette.core.mqtt.flows

import akka.stream.scaladsl.BidiFlow
import com.typesafe.scalalogging.{StrictLogging, Logger}
import org.beerfactory.casquette.core.mqtt.packets.MQTTPacket
import org.slf4j.LoggerFactory

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
