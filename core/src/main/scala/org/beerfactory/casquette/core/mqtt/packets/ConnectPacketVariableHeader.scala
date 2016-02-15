package org.beerfactory.casquette.core.mqtt.packets

import org.beerfactory.casquette.core.mqtt.QualityOfService._
import org.beerfactory.casquette.core.mqtt.codecs.MQTTCodecs._
import scodec.bits.BitVector
import scodec.codecs._
import scodec.bits._

/**
  * Created by nico on 15/02/2016.
  */
case class ConnectPacketVariableHeader(userNameFlag: Boolean,
                                       passwordFlag: Boolean,
                                       willRetainFlag: Boolean,
                                       willQos: QualityOfService,
                                       willFlag: Boolean,
                                       cleanSessionFlag: Boolean,
                                       keepAlive: Int)
object ConnectPacketVariableHeader {
  // MQTT 3.1.1 protocol name + version level constant
  val mqtt311HeaderConstant:BitVector = stringCodec.encode("MQTT").require ++ bin"00000100"
  implicit val codec = (
    constant(mqtt311HeaderConstant) :~>:
      bool ::
      bool ::
      bool ::
      qosCodec ::
      bool ::
      bool ::
      ignore(1) ::
      uint16).as[ConnectPacketVariableHeader]
}
