package org.beerfactory.casquette.core.mqtt.flows

import akka.stream.scaladsl.BidiFlow
import org.beerfactory.casquette.core.mqtt.packets._
import scodec.Codec
import scodec.codecs._


/**
  * Created by nico on 17/02/2016.
  */
object CodecFlow {
  def codecFlow = BidiFlow.fromFunctions(Codec[MQTTPacket].encode, Codec[MQTTPacket].decode)
}
