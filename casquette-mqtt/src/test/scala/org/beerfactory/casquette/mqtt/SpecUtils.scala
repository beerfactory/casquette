package org.beerfactory.casquette.mqtt

import org.specs2.matcher.{Expectable, Matcher}
import scodec.{Attempt, Err}

/**
  * Created by nico on 14/02/2016.
  */
object SpecUtils {
  class SuccessfulAttemptMatcher[T](v: T) extends Matcher[Attempt[T]] {
    def apply[S <: Attempt[T]](e: Expectable[S]) = {
      result(
        e.value.fold(_ => false, _ == v),
        s"${e.description} equals to $v",
        s"The result is ${e.description}, instead of the expected value '$v'",
        e)
    }
  }

  class FailedAttemptMatcher[T](m: Err) extends Matcher[Attempt[T]] {
    def apply[S <: Attempt[T]](e: Expectable[S]) = {
      result(
        e.value.fold(_ => true, _ != m),
        s"${e.description} equals to $m",
        s"The result is ${e.description} instead of the expected error message '$m'",
        e)
    }
  }

  def succeedWith[T](t: T) = new SuccessfulAttemptMatcher[T](t)

  def failWith[T](t: Err) = new FailedAttemptMatcher[T](t)

}
