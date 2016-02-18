package org.beerfactory.casquette.mqtt.codecs

import scodec.Attempt._
import scodec._
import scodec.bits.{BitVector, ByteVector}
import scodec.codecs._

/**
  * Created by nico on 08/02/2016.
  */

final class RemainingLengthCodec extends Codec[Int] {
  val MIN_VALUE = 0
  val MAX_VALUE = 268435455

  override def sizeBound = SizeBound.bounded(8L, 32L)

  override def encode(value: Int): Attempt[BitVector] = {
    if (value < MIN_VALUE || value > MAX_VALUE)
      failure(Err(s"Remaining length must be in the range [$MIN_VALUE..$MAX_VALUE]: $value is not valid"))
    else
      successful(encodeLength(value).bits)
  }

  override def decode(bits: BitVector): Attempt[DecodeResult[Int]] = {
    decodeLength(uint8.decode(bits))

  }

  private def decodeLength(nextByte: Attempt[DecodeResult[Int]], value:Int = 0, multiplier:Int = 1): Attempt[DecodeResult[Int]] = {

    if (multiplier > 128 * 128 * 128)
      failure(Err("The remaining length packet is too long"))
    nextByte match {
      case f: Failure ⇒ f
      case Successful(d) ⇒
        if ((d.value & 128) == 0)
          successful(DecodeResult(value + (d.value & 127) * multiplier, d.remainder))
        else
          decodeLength(uint8.decode(d.remainder), value + (d.value & 127) * multiplier, multiplier * 128)
    }
  }

  private def encodeLength(value: Int) : ByteVector = {
    val remaining = value / 128
    val encodedByte = (value % 128).asInstanceOf[Byte]
    if (remaining == 0)
      ByteVector(encodedByte)
    else
      (if (remaining > 0) ByteVector(encodedByte | 128) else ByteVector(encodedByte)) ++ encodeLength(remaining)
  }

}
