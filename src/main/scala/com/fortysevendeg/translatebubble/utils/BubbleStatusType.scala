package com.fortysevendeg.translatebubble.utils

sealed trait BubbleStatusType

object BubbleStatusType {
  def unapply(t: BubbleStatusType): String = t.toString
}

case object BubbleStatusFloating extends BubbleStatusType

case object BubbleStatusContent extends BubbleStatusType