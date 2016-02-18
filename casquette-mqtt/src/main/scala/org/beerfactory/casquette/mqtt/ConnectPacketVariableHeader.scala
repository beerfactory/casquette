package org.beerfactory.casquette.mqtt

import org.beerfactory.casquette.mqtt.QualityOfService.QualityOfService
import org.beerfactory.casquette.mqtt.codecs.MQTTCodecs._
import scodec.bits.{BitVector, _}
import scodec.codecs._

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
