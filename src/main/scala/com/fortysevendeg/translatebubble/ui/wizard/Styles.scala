package com.fortysevendeg.translatebubble.ui.wizard

import android.view.Gravity
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.translatebubble.R
import macroid.AppContext
import macroid.FullDsl._
import scala.language.postfixOps

object Styles {

  val rootStyle = vMatchParent

  val pagerStyle = vMatchParent

  def contentStepsStyle(implicit appContext: AppContext) = vMatchParent + llVertical + vPaddings(8 dp)

  def placeHolderStyle(implicit appContext: AppContext) = vWrapContent + vPaddings(8 dp)

  def titleStepStyle(implicit appContext: AppContext) = vWrapContent + vPaddings(8 dp) + tvSize(24) + tvBoldCondensed

  def descriptionStepStyle(implicit appContext: AppContext) = vWrapContent + vPaddings(8 dp) + tvSize(17)

  def agreeStepStyle(implicit appContext: AppContext) = vWrapContent + llLayoutGravity(Gravity.CENTER) +
      vPadding(8 dp, 0 dp, 8 dp, 0 dp) + tvSize(12) + tvText(R.string.agree) +
      tvColor(appContext.get.getResources.getColor(R.color.agree_button)) +
      vBackgroundColor(appContext.get.getResources.getColor(R.color.accent))

}
