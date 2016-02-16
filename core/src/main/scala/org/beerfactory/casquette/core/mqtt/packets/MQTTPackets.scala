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
case class ConnAckPacket(sessionPresentFlag: Boolean, returnCode: Byte) extends MQTTPacket
case class PublishPacket(fixedHeader: PublishPacketFixedHeader, topic: String, packetIdentifier: Option[Short], payload: ByteVector) extends MQTTPacket
case class PubAckPacket(packetIdentifier: Short) extends MQTTPacket
case class PubRecPacket(packetIdentifier: Short) extends MQTTPacket
case class PubRelPacket(packetIdentifier: Short) extends MQTTPacket
case class PubCompPacket(packetIdentifier: Short) extends MQTTPacket
case class SubscribePacket(packetIdentifier: Short, topics: Vector[(String, QualityOfService)]) extends MQTTPacket
case class SubAckPacket(packetIdentifier: Short, returnCodes: Vector[Byte]) extends MQTTPacket
case class UnSubscribePacket(packetIdentifier: Short, topics: Vector[String]) extends MQTTPacket
case class UnSubAckPacket(packetIdentifier: Short) extends MQTTPacket
case class PingReqPacket() extends MQTTPacket
case class PingRespPacket() extends MQTTPacket
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

object ConnAckPacket {
  implicit val discriminator: Discriminator[MQTTPacket, ConnAckPacket, Byte] = Discriminator(2)
  implicit val codec: Codec[ConnAckPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec,
      ignore(7) ::
        bool ::
        byte)
  ).dropUnits.as[ConnAckPacket]
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

object PubAckPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubAckPacket, Byte] = Discriminator(4)
  implicit val codec: Codec[PubAckPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubAckPacket]
}

object PubRecPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubRecPacket, Byte] = Discriminator(5)
  implicit val codec: Codec[PubRecPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubRecPacket]
}

object PubRelPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubRelPacket, Byte] = Discriminator(6)
  implicit val codec: Codec[PubRelPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubRelPacket]

}

object PubCompPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PubCompPacket, Byte] = Discriminator(7)
  implicit val codec: Codec[PubCompPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[PubCompPacket]
}

object SubscribePacket {
  implicit val discriminator: Discriminator[MQTTPacket, SubscribePacket, Byte] = Discriminator(8)
  val topicCodec = (stringCodec :: ignore(6) :: qosCodec).dropUnits.as[(String, QualityOfService)]
  val topicsCodec: Codec[Vector[(String, QualityOfService)]] = vector(topicCodec)
  implicit val codec: Codec[SubscribePacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec :: topicsCodec)).as[SubscribePacket]
}

object SubAckPacket {
  implicit val discriminator: Discriminator[MQTTPacket, SubAckPacket, Byte] = Discriminator(9)
  val returnCodesCodec: Codec[Vector[Byte]] = vector(byte)
  implicit val codec: Codec[SubAckPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec :: returnCodesCodec)).as[SubAckPacket]
}

object UnSubscribePacket {
  implicit val discriminator: Discriminator[MQTTPacket, UnSubscribePacket, Byte] = Discriminator(10)
  val topicsCodec: Codec[Vector[String]] = vector(stringCodec)
  implicit val codec: Codec[UnSubscribePacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec :: topicsCodec)).as[UnSubscribePacket]
}

object UnSubAckPacket {
  implicit val discriminator: Discriminator[MQTTPacket, UnSubAckPacket, Byte] = Discriminator(11)
  implicit val codec: Codec[UnSubAckPacket] = (DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, packetIdCodec)).as[UnSubAckPacket]
}

object PingReqPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PingReqPacket, Byte] = Discriminator(12)
  implicit val codec: Codec[PingReqPacket] =(DefaultFixedHeader.codec ::
    variableSizeBytes(remainingLengthCodec, ignore(0))).dropUnits.as[PingReqPacket]
}

object PingRespPacket {
  implicit val discriminator: Discriminator[MQTTPacket, PingRespPacket, Byte] = Discriminator(13)
  implicit val codec: Codec[PingRespPacket] =(DefaultFixedHeader.codec ::
      variableSizeBytes(remainingLengthCodec, ignore(0))).dropUnits.as[PingRespPacket]
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
