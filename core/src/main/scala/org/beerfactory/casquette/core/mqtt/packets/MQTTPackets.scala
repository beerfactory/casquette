package org.beerfactory.casquette.core.mqtt.packets

import org.beerfactory.casquette.core.mqtt.QualityOfService._
import org.beerfactory.casquette.core.mqtt.codecs.MQTTCodecs._
import scodec.Codec
import scodec.bits._
import scodec.codecs._

/**
  * Created by nico on 07/02/2016.
  */

sealed trait MQTTPacket

case class ConnectPacket(variableHeader: ConnectPacketVariableHeader,
                         clientId: String,
                         willTopic: Option[String],
                         willMessage: Option[String],
                         userName: Option[String],
                         password: Option[String]
                         ) extends MQTTPacket
case class ConnackPacket(sessionPresentFlag: Boolean, returnCode: Byte) extends MQTTPacket
case class PublishPacket(fixedHeader: PublishPacketFixedHeader, topic: String, packetIdentifier: Option[Int], payload: ByteVector) extends MQTTPacket
case class PubackPacket(packetIdentifier: Int) extends MQTTPacket
case class PubrecPacket(packetIdentifier: Int) extends MQTTPacket
case class PubrelPacket(packetIdentifier: Int) extends MQTTPacket
case class PubcompPacket(packetIdentifier: Int) extends MQTTPacket

object ConnectPacket {
  implicit val discriminator: Discriminator[MQTTPacket, ConnectPacket, Int] = Discriminator(1)
  implicit val codec: Codec[ConnectPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec,
      ConnectPacketVariableHeader.codec >>:~ { (header: ConnectPacketVariableHeader) ⇒
        stringCodec ::
          conditional(header.willFlag, stringCodec) ::
          conditional(header.willFlag, stringCodec) ::
          conditional(header.userNameFlag, stringCodec) ::
          conditional(header.passwordFlag, stringCodec)
      })).as[ConnectPacket]
}

object ConnackPacket {
  implicit val discriminator: Discriminator[MQTTPacket, ConnackPacket, Int] = Discriminator(2)
  implicit val codec: Codec[ConnackPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec,
      ignore(7) ::
        bool ::
        byte)
  ).dropUnits.as[ConnackPacket]
}

object PublishPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PublishPacket, Int] = Discriminator(3)
  implicit val codec: Codec[PublishPacket] = (PublishPacketFixedHeader.codec >>:~ {
    (header: PublishPacketFixedHeader) ⇒
      variableSizeBytes(remainingLengthCodec,
        stringCodec ::
        conditional(header.qos != QOS_0, packetIdCodec) ::
        bytes
      )}
    ).as[PublishPacket]
}

object PubackPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubackPacket, Int] = Discriminator(4)
  implicit val codec: Codec[PubackPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubackPacket]
}

object PubrecPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubrecPacket, Int] = Discriminator(5)
  implicit val codec: Codec[PubrecPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubrecPacket]
}

object PubrelPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubrelPacket, Int] = Discriminator(6)
  implicit val codec: Codec[PubrelPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubrelPacket]

}

object PubcompPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubcompPacket, Int] = Discriminator(7)
  implicit val codec: Codec[PubcompPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubcompPacket]
}

//Companion object moved to bottom of file according to :
// http://stackoverflow.com/questions/30835502/scodec-coproducts-could-not-find-implicit-value-for-parameter-auto-scodec-code
object MQTTPacket {
  implicit val discriminated: Discriminated[MQTTPacket, Int] = Discriminated(uint4)
  implicit val codec = Codec.coproduct[MQTTPacket].discriminatedBy(uint4).auto
}
