package com.fortysevendeg.translatebubble.ui.history

import android.support.v7.widget.CardView
import android.text.TextUtils.TruncateAt
import android.view.Gravity
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.R
import macroid.{ActivityContext, AppContext, Tweak}

import scala.language.postfixOps

trait Styles {

  def cardStyle(implicit activityContext: ActivityContext, appContext: AppContext): Tweak[CardView] =
    vMatchWidth

  def translateStyle(implicit appContext: AppContext) =
    vMatchWidth +
        llVertical +
        vPadding(
          paddingBottom = resGetDimensionPixelSize(R.dimen.padding_default),
          paddingLeft = resGetDimensionPixelSize(R.dimen.padding_history_content),
          paddingRight = resGetDimensionPixelSize(R.dimen.padding_history_content),
          paddingTop = resGetDimensionPixelSize(R.dimen.padding_default))

  def translateTitleStyle(implicit appContext: AppContext) =
    vMatchWidth +
        llHorizontal +
        llGravity(Gravity.CENTER_VERTICAL)

  def languagesStyle(implicit appContext: AppContext) =
    vMatchParent +
        tvColor(resGetColor(R.color.history_text_title)) +
        tvEllipsize(TruncateAt.END) +
        tvMaxLines(1) +
        tvSize(resGetInteger(R.integer.text_big))

  def originalTextStyle(implicit appContext: AppContext) =
    textStyle +
        vMatchWidth +
        tvMaxLines(1) +
        vPadding(
          paddingBottom = resGetDimensionPixelSize(R.dimen.padding_default),
          paddingTop = resGetDimensionPixelSize(R.dimen.padding_default))

  def translatedTextStyle(implicit appContext: AppContext) =
    textStyle +
        llMatchWeightVertical

  def lineStyle(implicit appContext: AppContext) =
    vContentSizeMatchWidth(resGetDimensionPixelSize(R.dimen.line_stroke)) +
        vBackgroundColor(R.color.line_content)

  def textStyle(implicit appContext: AppContext) =
    tvColor(resGetColor(R.color.history_text_content)) +
        tvEllipsize(TruncateAt.END) +
        tvSize(resGetInteger(R.integer.text_medium))
}
