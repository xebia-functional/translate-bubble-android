/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortysevendeg.translatebubble.ui.history

import android.support.v7.widget.CardView
import android.text.TextUtils.TruncateAt
import android.view.Gravity
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.R
import macroid.{ContextWrapper, Tweak}

import scala.language.postfixOps

trait Styles {

  def cardStyle(implicit contextWrapper: ContextWrapper): Tweak[CardView] =
    vMatchWidth

  def translateStyle(implicit contextWrapper: ContextWrapper) =
    vMatchWidth +
      llVertical +
      vPadding(
        paddingBottom = resGetDimensionPixelSize(R.dimen.padding_default),
        paddingLeft = resGetDimensionPixelSize(R.dimen.padding_history_content),
        paddingRight = resGetDimensionPixelSize(R.dimen.padding_history_content),
        paddingTop = resGetDimensionPixelSize(R.dimen.padding_default))

  def translateTitleStyle(implicit contextWrapper: ContextWrapper) =
    vMatchWidth +
      llHorizontal +
      llGravity(Gravity.CENTER_VERTICAL)

  def languagesStyle(implicit contextWrapper: ContextWrapper) =
    vMatchParent +
      tvColor(resGetColor(R.color.history_text_title)) +
      tvEllipsize(TruncateAt.END) +
      tvMaxLines(1) +
      tvSize(resGetInteger(R.integer.text_big))

  def originalTextStyle(implicit contextWrapper: ContextWrapper) =
    textStyle +
      vMatchWidth +
      tvMaxLines(1) +
      vPadding(
        paddingBottom = resGetDimensionPixelSize(R.dimen.padding_default),
        paddingTop = resGetDimensionPixelSize(R.dimen.padding_default))

  def translatedTextStyle(implicit contextWrapper: ContextWrapper) =
    textStyle +
      llMatchWeightVertical

  def lineStyle(implicit contextWrapper: ContextWrapper) =
    vContentSizeMatchWidth(resGetDimensionPixelSize(R.dimen.line_stroke)) +
      vBackgroundColor(R.color.line_content)

  def textStyle(implicit contextWrapper: ContextWrapper) =
    tvColor(resGetColor(R.color.history_text_content)) +
      tvEllipsize(TruncateAt.END) +
      tvSize(resGetInteger(R.integer.text_medium))
}
