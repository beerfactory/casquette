package org.beerfactory.casquette.core.packet

import org.scalatest.FlatSpec
import scodec.Attempt
import scodec.bits._

/**
  * Created by nico on 09/02/2016.
  */
class RemainingLengthCodecSpec extends FlatSpec {
  "RemainingLengthCodecSpec encode" should "succeed with result 0x7f for length 127" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(127) == Attempt.successful(hex"7f".bits))
  }

  it should "succeed with result 0x8001 for length 128" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(128) == Attempt.successful(hex"8001".bits))
  }

  it should "succeed with result 0xff7f for length 16383" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(16383) == Attempt.successful(hex"ff7f".bits))
  }

  it should "succeed with result 0x808001 for length 16384" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(16384) == Attempt.successful(hex"808001".bits))
  }

  it should "succeed with result 0xffff7f for length 2097151" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(2097151) == Attempt.successful(hex"ffff7f".bits))
  }

  it should "succeed with result 0x80808001 for length 2097152" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(2097152) == Attempt.successful(hex"80808001".bits))
  }

  it should "succeed with result 0xffffff7f for length 268435455" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(268435455) == Attempt.successful(hex"ffffff7f".bits))
  }

  it should "fail if value < 0" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(-1).isFailure)
  }

  it should "fail if value > 268435455" in {
    val codec = new RemainingLengthCodec
    assert(codec.encode(999999999).isFailure)
  }

  "RemainingLengthCodecSpec decode" should "succeed with result 127 for encoded value 0x7f" in {
    val codec = new RemainingLengthCodec
    assert(codec.decode(hex"7f".bits).require.value == 127)
  }

  it should "succeed with result 128 for encoded value 0x8001" in {
    val codec = new RemainingLengthCodec
    assert(codec.decode(hex"8001".bits).require.value == 128)
  }

  it should "succeed with result 16383 for encoded value 0xff7f" in {
    val codec = new RemainingLengthCodec
    assert(codec.decode(hex"ff7f".bits).require.value == 16383)
  }

  it should "succeed with result 16384 for encoded value 0x808001" in {
    val codec = new RemainingLengthCodec
    assert(codec.decode(hex"808001".bits).require.value == 16384)
  }

  it should "succeed with result 2097151 for encoded value 0xffff7f" in {
    val codec = new RemainingLengthCodec
    assert(codec.decode(hex"ffff7f".bits).require.value == 2097151)
  }

  it should "succeed with result 2097152 for encoded value 0x80808001" in {
    val codec = new RemainingLengthCodec
    assert(codec.decode(hex"80808001".bits).require.value == 2097152)
  }

  it should "succeed with result 268435455 for encoded value 0xffffff7f" in {
    val codec = new RemainingLengthCodec
    assert(codec.decode(hex"ffffff7f".bits).require.value == 268435455)
  }

  it should "fail if encoded value > 0xffffff7f" in {
    val codec = new RemainingLengthCodec
    assert(codec.decode(hex"ffffffff".bits).isFailure)
  }

}
