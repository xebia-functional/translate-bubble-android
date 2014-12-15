package com.fortysevendeg.translatebubble.macroid

import macroid.AppContext
import macroid.FullDsl._

trait DevicesQueries {
  def tablet(implicit ctx: AppContext) = widerThan(720 dp)
  def landscapeTablet(implicit ctx: AppContext) = widerThan(720 dp) & landscape
  def portraitTablet(implicit ctx: AppContext) = widerThan(720 dp) & portrait
}
object DevicesQueries extends DevicesQueries
