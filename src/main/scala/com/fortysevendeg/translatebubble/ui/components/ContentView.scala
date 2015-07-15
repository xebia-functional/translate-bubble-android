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

package com.fortysevendeg.translatebubble.ui.components

import android.animation.{AnimatorListenerAdapter, Animator, ValueAnimator}
import android.content.Context
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams._
import android.view._
import android.widget.{FrameLayout, ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.R
import macroid.FullDsl._
import macroid._

import scala.language.postfixOps

class ContentView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ServiceContextWrapper)
    extends FrameLayout(context, attrs, defStyleAttr)
    with ContentViewLayout {

  def this(context: Context)(implicit contextWrapper: ServiceContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ServiceContextWrapper) = this(context, attr, 0)

  addView(layout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

  private[this] def updateLayout(params: WindowManager.LayoutParams, windowManager: WindowManager) = Ui {
    windowManager.updateViewLayout(this, params)
  }

  def setTexts(langs: String, textOriginal: String, textTranslate: String): Ui[_] =
    (languages <~ tvText(langs)) ~ (original <~ tvText(textOriginal)) ~ (translate <~ tvText(textTranslate))

  def show(): Ui[_] = this <~ vVisible

  def hide(): Ui[_] = this <~ vInvisible

  def collapse(params: WindowManager.LayoutParams, windowManager: WindowManager): Ui[_] = Ui {
    val animator: ValueAnimator = ValueAnimator.ofFloat(0, 100)
    animator.setDuration(100)
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) = {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.alpha = 1 - (pos / 100)
        runUi(updateLayout(params, windowManager))
      }
    })
    animator.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator) = {
        params.alpha = 1
        runUi(hide() ~ updateLayout(params, windowManager))
      }
    })
    animator.start()
  }

  def changePositionIfIsNecessary(
      widthScreen: Int,
      heightScreen: Int,
      params: WindowManager.LayoutParams,
      windowManager: WindowManager): Ui[_] = {
    if (params.x + getWidth > widthScreen) {
      params.x = widthScreen - getWidth
    }
    if (params.y + getHeight > heightScreen) {
      params.y = heightScreen - getHeight
    }
    updateLayout(params, windowManager)
  }

}

trait ContentViewLayout {

  var languages = slot[TextView]

  var options = slot[ImageView]

  var original = slot[TextView]

  var translate = slot[TextView]

  def layout(implicit contextWrapper: ServiceContextWrapper) = getUi(
    l[LinearLayout](
      l[LinearLayout](
        w[TextView] <~ wire(languages) <~ tittleLanguagesStyle,
        w[ImageView] <~ wire(options) <~ optionsStyle
      ) <~ titleContentStyle,
      w[TextView] <~ wire(original) <~ originalTextStyle,
      w[ImageView] <~ lineStyle,
      w[TextView] <~ wire(translate) <~ translateTextStyle
    ) <~ rootStyle
  )

  def rootStyle(implicit contextWrapper: ContextWrapper): Tweak[LinearLayout] =
    vMatchParent +
        llVertical +
        vBackground(R.drawable.box)

  def titleContentStyle(implicit contextWrapper: ContextWrapper): Tweak[LinearLayout] =
    vMatchWidth +
        llHorizontal +
        llLayoutMargin(marginBottom = resGetDimensionPixelSize(R.dimen.margin_default)) +
        llGravity(Gravity.CENTER_VERTICAL)

  def tittleLanguagesStyle(implicit contextWrapper: ContextWrapper): Tweak[TextView] =
    llWrapWeightHorizontal +
        vPadding(paddingBottom = resGetDimensionPixelSize(R.dimen.margin_default)) +
        tvColorResource(R.color.languages_content_light) +
        tvSizeResource(R.dimen.languages_content) +
        tvLines(1) +
        tvEllipsize(TruncateAt.END) +
        tvBold

  def optionsStyle(implicit contextWrapper: ContextWrapper): Tweak[ImageView] =
    vWrapContent +
        llLayoutGravity(Gravity.TOP | Gravity.RIGHT) +
        ivSrc(R.drawable.box_icon_close)

  def originalTextStyle(implicit contextWrapper: ContextWrapper): Tweak[TextView] =
    vMatchWidth +
        tvColorResource(R.color.title_content_light) +
        tvSizeResource(R.dimen.text_content) +
        tvLines(1) +
        tvEllipsize(TruncateAt.END)

  def lineStyle(implicit contextWrapper: ContextWrapper): Tweak[ImageView] = {
    val padding = resGetDimensionPixelSize(R.dimen.padding_default)
    lp[ViewGroup](MATCH_PARENT, resGetDimensionPixelSize(R.dimen.line_stroke)) +
        llLayoutMargin(marginTop = padding, marginBottom = padding) +
        vBackgroundColorResource(R.color.line_content)
  }

  def translateTextStyle(implicit contextWrapper: ContextWrapper): Tweak[TextView] =
    llMatchWeightVertical +
        tvColorResource(R.color.text_content_light) +
        tvSizeResource(R.dimen.text_content) +
        tvEllipsize(TruncateAt.END)

}
