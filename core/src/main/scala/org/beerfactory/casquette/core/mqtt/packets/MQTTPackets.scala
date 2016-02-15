package org.beerfactory.casquette.core.mqtt.packets

import org.beerfactory.casquette.core.mqtt.QualityOfService._
import org.beerfactory.casquette.core.mqtt.codecs.MQTTCodecs._
import scodec.Codec
import scodec.bits._
import scodec.codecs._

/**
  * Created by nico on 07/02/2016.
  */

sealed trait MQTTPacket {
  def fixedHeader: MQTTFixedHeader
}
object MQTTPacket {
  implicit val discriminated: Discriminated[MQTTPacket, Int] = Discriminated(uint4)
}

case class ConnectPacket(fixedHeader: ConnectPacketFixedHeader,
                         variableHeader: ConnectPacketVariableHeader,
                         clientId: String,
                         willTopic: Option[String],
                         willMessage: Option[String],
                         userName: Option[String],
                         password: Option[String]
                         ) extends MQTTPacket
//case class


object ConnectPacket {
  implicit val disrcriminator: Discriminator[MQTTPacket, ConnectPacket, Int] = Discriminator(1)
  implicit val codec: Codec[ConnectPacket] = (ConnectPacketFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec,
      ConnectPacketVariableHeader.codec >>:~ { (header: ConnectPacketVariableHeader) ⇒
        stringCodec ::
          conditional(header.willFlag, stringCodec) ::
          conditional(header.willFlag, stringCodec) ::
          conditional(header.userNameFlag, stringCodec) ::
          conditional(header.passwordFlag, stringCodec)
      })).as[ConnectPacket]
}
