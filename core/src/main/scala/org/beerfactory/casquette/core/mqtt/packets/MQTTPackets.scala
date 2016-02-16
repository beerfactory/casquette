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
case class PublishPacket(fixedHeader: PublishPacketFixedHeader, topic: String, packetIdentifier: Option[Short], payload: ByteVector) extends MQTTPacket
case class PubackPacket(packetIdentifier: Short) extends MQTTPacket
case class PubrecPacket(packetIdentifier: Short) extends MQTTPacket
case class PubrelPacket(packetIdentifier: Short) extends MQTTPacket
case class PubcompPacket(packetIdentifier: Short) extends MQTTPacket
case class SubscribePacket(packetIdentifier: Short, topics: Vector[(String, QualityOfService)]) extends MQTTPacket
case class SubackPacket(packetIdentifier: Short, returnCodes: Vector[Byte]) extends MQTTPacket
case class UnsubscribePacket(packetIdentifier: Short, topics: Vector[String]) extends MQTTPacket
case class UnsubackPacket(packetIdentifier: Short) extends MQTTPacket
case class PingreqPacket() extends MQTTPacket
case class PingrespPacket() extends MQTTPacket
case class DisconnectPacket() extends MQTTPacket

object ConnectPacket {
  implicit val discriminator: Discriminator[MQTTPacket, ConnectPacket, Byte] = Discriminator(1)
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
  implicit val discriminator: Discriminator[MQTTPacket, ConnackPacket, Byte] = Discriminator(2)
  implicit val codec: Codec[ConnackPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec,
      ignore(7) ::
        bool ::
        byte)
  ).dropUnits.as[ConnackPacket]
}

object PublishPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PublishPacket, Byte] = Discriminator(3)
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
  implicit val discriminator: Discriminator[MQTTPacket, PubackPacket, Byte] = Discriminator(4)
  implicit val codec: Codec[PubackPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubackPacket]
}

object PubrecPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubrecPacket, Byte] = Discriminator(5)
  implicit val codec: Codec[PubrecPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubrecPacket]
}

object PubrelPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubrelPacket, Byte] = Discriminator(6)
  implicit val codec: Codec[PubrelPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubrelPacket]

}

object PubcompPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubcompPacket, Byte] = Discriminator(7)
  implicit val codec: Codec[PubcompPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubcompPacket]
}

object SubscribePacket {
  implicit val discriminator: Discriminator[MQTTPacket, SubscribePacket, Byte] = Discriminator(8)
  val topicCodec = (stringCodec :: ignore(6) :: qosCodec).dropUnits.as[(String, QualityOfService)]
  val topicsCodec: Codec[Vector[(String, QualityOfService)]] = vector(topicCodec)
  implicit val codec: Codec[SubscribePacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec :: topicsCodec)).as[SubscribePacket]
}

object SubackPacket {
  implicit val discriminator: Discriminator[MQTTPacket, SubackPacket, Byte] = Discriminator(9)
  val returnCodesCodec: Codec[Vector[Byte]] = vector(byte)
  implicit val codec: Codec[SubackPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec :: returnCodesCodec)).as[SubackPacket]
}

object UnsubscribePacket {
  implicit val discriminator: Discriminator[MQTTPacket, UnsubscribePacket, Byte] = Discriminator(10)
  val topicsCodec: Codec[Vector[String]] = vector(stringCodec)
  implicit val codec: Codec[UnsubscribePacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec :: topicsCodec)).as[UnsubscribePacket]
}

object UnsubackPacket {
  implicit val discriminator: Discriminator[MQTTPacket, UnsubackPacket, Byte] = Discriminator(11)
  implicit val codec: Codec[UnsubackPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[UnsubackPacket]
}

object PingreqPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PingreqPacket, Byte] = Discriminator(12)
  implicit val codec: Codec[PingreqPacket] =(DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, ignore(0))).dropUnits.as[PingreqPacket]
}

object PingrespPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PingrespPacket, Byte] = Discriminator(13)
  implicit val codec: Codec[PingrespPacket] =(DefaultFixedHeader.codec ::
      variableSizeBytes(remainingLengthCodec, ignore(0))).dropUnits.as[PingrespPacket]
}

object DisconnectPacket {
  implicit val discriminator: Discriminator[MQTTPacket, DisconnectPacket, Byte] = Discriminator(14)
  implicit val codec: Codec[DisconnectPacket] =(DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, ignore(0))).dropUnits.as[DisconnectPacket]
}

//Companion object moved to bottom of file according to :
// http://stackoverflow.com/questions/30835502/scodec-coproducts-could-not-find-implicit-value-for-parameter-auto-scodec-code
object MQTTPacket {
  implicit val discriminated: Discriminated[MQTTPacket, Byte] = Discriminated(ubyte(4))
  implicit val codec = Codec.coproduct[MQTTPacket].discriminatedBy(ubyte(4)).auto
}
