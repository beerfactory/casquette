package org.beerfactory.casquette.core.mqtt

import org.beerfactory.casquette.core.packet.QualityOfService
import org.beerfactory.casquette.core.packet.QualityOfService.QualityOfService
import scodec.Attempt._
import scodec._
import scodec.bits._
import scodec.codecs._


/**
  * Created by nico on 13/02/2016.
  */
class QualityOfServiceCodec extends Codec[QualityOfService] {
  override def sizeBound = SizeBound.exact(2L)

  override def encode(value: QualityOfService): Attempt[BitVector] = {
    value match {
      case QualityOfService.QOS_0 => successful(bin"00")
      case QualityOfService.QOS_1 => successful(bin"01")
      case QualityOfService.QOS_2 => successful(bin"10")
      case _ => failure(Err("$value is not a valid encoded QOS value"))
    }
  }

  override def decode(bits: BitVector): Attempt[DecodeResult[QualityOfService]] = {
    uint2.decode(bits) match {
      case f:Failure => f
      case Successful(r) =>
        try {
          successful(DecodeResult(QualityOfService.fromInt(r.value), r.remainder))
        }
        catch {
          case iae: IllegalArgumentException => failure(Err(s"Invalid QOS value $r.value"))
        }
    }
  }

}
