package com.fortysevendeg.translatebubble.utils

trait OptionOps {

  implicit class RichOptionException[A](option: Option[A]) {

    def orThrow(e: Exception): Option[A] = option orElse (throw e)

    def flattenOr(e: Exception): A = option getOrElse (throw e)

  }

}
