package org.beerfactory.casquette.core

import org.specs2.matcher.{Expectable, Matcher}
import scodec.{Attempt, Codec}

/**
  * Created by nico on 14/02/2016.
  */
object SpecUtils {
  class EncodingMatcher[T](codec: Codec[T])(v: T) extends Matcher[Attempt[T]] {
    def apply[S <: Attempt[T]](e: Expectable[S]) = {
      val encoded = codec.encode(v)
      result(
        e.value.fold(_ => false, _ == encoded),
        s"${e.description} equals to $v",
        s"The result is ${e.description}, instead of the expected value '$v'",
        e)
    }
  }

  class WithEncoder[T](v: T) {
    def withCodec(codec: Codec[T]): Matcher[Attempt[T]] = {
      new EncodingMatcher[T](codec)
    }
  }

  def beEncodedAs[T](result: T) = new WithEncoder(result)
}
