package org.beerfactory.casquette.mqtt.codecs

import org.beerfactory.casquette.mqtt.SpecUtils
import SpecUtils._
import MQTTCodecs._
import org.specs2.mutable.Specification
import scodec.bits._
import scodec.{DecodeResult, Err}

/**
  * Created by nico on 09/02/2016.
  */
class RemainingLengthCodecSpec extends Specification {
  "RemainingLengthCodec codec" should  {
    "succeed encoding valid length values" in  {
      remainingLengthCodec.encode(127) must succeedWith(hex"7f".bits)
      remainingLengthCodec.encode(128) must succeedWith(hex"8001".bits)
      remainingLengthCodec.encode(16383) must succeedWith(hex"ff7f".bits)
      remainingLengthCodec.encode(16384) must succeedWith(hex"808001".bits)
      remainingLengthCodec.encode(2097151) must succeedWith(hex"ffff7f".bits)
      remainingLengthCodec.encode(2097152) must succeedWith(hex"80808001".bits)
      remainingLengthCodec.encode(268435455) must succeedWith(hex"ffffff7f".bits)
    }
    "fail encoding invalid length values" in {
      remainingLengthCodec.encode(-1) must failWith(Err(s"Remaining length must be in the range [0..268435455]: -1 is not valid"))
      remainingLengthCodec.encode(999999999) must failWith(Err(s"Remaining length must be in the range [0..268435455]: 999999999 is not valid"))
    }
    "succeed decoding valid length bytes" in {
      remainingLengthCodec.decode(hex"00".bits) should succeedWith(DecodeResult(0, BitVector.empty))
      remainingLengthCodec.decode(hex"7f".bits) should succeedWith(DecodeResult(127, BitVector.empty))
      remainingLengthCodec.decode(hex"8001".bits) should succeedWith(DecodeResult(128, BitVector.empty))
      remainingLengthCodec.decode(hex"ff7f".bits) should succeedWith(DecodeResult(16383, BitVector.empty))
      remainingLengthCodec.decode(hex"808001".bits) should succeedWith(DecodeResult(16384, BitVector.empty))
      remainingLengthCodec.decode(hex"ffff7f".bits) should succeedWith(DecodeResult(2097151, BitVector.empty))
      remainingLengthCodec.decode(hex"80808001".bits) should succeedWith(DecodeResult(2097152, BitVector.empty))
      remainingLengthCodec.decode(hex"ffffff7f".bits) should succeedWith(DecodeResult(268435455, BitVector.empty))
    }
    "fail decoding invalid length bytes" in {
      remainingLengthCodec.decode(hex"ffffffff".bits) should failWith(Err("The remaining length packet is too long"))
    }
  }
}
