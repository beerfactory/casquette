package org.beerfactory.casquette.core.packet

import org.beerfactory.casquette.core.packet.QualityOfService.QualityOfService
import scodec.Codec
import scodec.bits._
import scodec.codecs._

/**
  * Created by nico on 07/02/2016.
  */

sealed trait MQTTPacket
case class FixedHeader(flag_0: Boolean, flag_1: Boolean, flag_2: Boolean, flag_3: Boolean)

case class ConnectPacketFixedHeader(dupFlag: Boolean, qos: QualityOfService, retain: Boolean)
case class ConnectPacketVariableHeader(userNameFlag: Boolean,
                                       passwordFlag: Boolean,
                                       willRetainFlag: Boolean,
                                       willQos: QualityOfService,
                                       willFlag: Boolean,
                                       cleanSessionFlag: Boolean,
                                       keepAlive: Int)
case class ConnectPacket(fixedHeader: ConnectPacketFixedHeader,
                         variableHeader: ConnectPacketVariableHeader,
                         clientId: String,
                         willTopic: Option[String],
                         willMessage: Option[String],
                         userName: Option[String],
                         password: Option[String]
                         ) extends MQTTPacket

// Codecs
object MQTTCodecs {
  val remainingLengthCodec = new RemainingLengthCodec
  val stringCodec = variableSizeBytes(uint16, utf8)
  val qosCodec = new QualityOfServiceCodec

}
object FixedHeader {
  implicit val codec = (bool :: bool :: bool :: bool).as[FixedHeader]
}
object ConnectPacketFixedHeader {
  implicit val codec = (bool :: MQTTCodecs.qosCodec :: bool).as[ConnectPacketFixedHeader]
}
object ConnectPacketVariableHeader {
  // MQTT 3.1.1 protocol name + version level constant
  val mqtt311HeaderConstant:BitVector = MQTTCodecs.stringCodec.encode("MQTT").require ++ bin"00000100"
  implicit val codec = (
    constant(mqtt311HeaderConstant) :~>:
    bool ::
    bool ::
    bool ::
    MQTTCodecs.qosCodec ::
    bool ::
    bool ::
    ignore(1) ::
    uint16).as[ConnectPacketVariableHeader]
}

object MQTTPacket {
  implicit val discriminated: Discriminated[MQTTPacket, Int] = Discriminated(uint4)
}

object ConnectPacket {
  implicit val disrcriminator: Discriminator[MQTTPacket, ConnectPacket, Int] = Discriminator(1)
  implicit val codec: Codec[ConnectPacket] = (ConnectPacketFixedHeader.codec ::
    variableSizeBytes(MQTTCodecs.remainingLengthCodec,
      ConnectPacketVariableHeader.codec >>:~ { (header: ConnectPacketVariableHeader) ⇒
        MQTTCodecs.stringCodec ::
          conditional(header.willFlag, MQTTCodecs.stringCodec) ::
          conditional(header.willFlag, MQTTCodecs.stringCodec) ::
          conditional(header.userNameFlag, MQTTCodecs.stringCodec) ::
          conditional(header.passwordFlag, MQTTCodecs.stringCodec)
      })).as[ConnectPacket]
}
