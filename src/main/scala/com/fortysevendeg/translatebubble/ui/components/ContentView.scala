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

import android.animation.{Animator, ValueAnimator}
import android.content.Context
import android.util.AttributeSet
import android.view.View._
import android.view._
import android.widget.{FrameLayout, ImageView, TextView}
import com.fortysevendeg.macroid.extras.LayoutBuildingExtra._
import com.fortysevendeg.macroid.extras.RootView
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.translatebubble.R
import macroid.AppContext
import macroid.FullDsl._

import scala.language.postfixOps

class ContentView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
    extends FrameLayout(context, attrs, defStyleAttr) {

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  implicit val rootView: RootView = new RootView(R.layout.content_view)

  val languages: Option[TextView] = connect[TextView](R.id.languages)
  val original: Option[TextView] = connect[TextView](R.id.original)
  val translate: Option[TextView] = connect[TextView](R.id.translate)
  val options: Option[ImageView] = connect[ImageView](R.id.options)

  addView(rootView.view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

  def setTexts(langs: String, textOriginal: String, textTranslate: String) {
    runUi(
      (languages <~ tvText(langs)) ~ (original <~ tvText(textOriginal)) ~ (translate <~ tvText(textTranslate))
    )
  }

  def show() {
    setVisibility(VISIBLE)
  }

  def hide() {
    setVisibility(INVISIBLE)
  }

  def collapse(params: WindowManager.LayoutParams, windowManager: WindowManager) {
    // TODO we can create a Snail when we have Macroid-Extra module
    val animator: ValueAnimator = ValueAnimator.ofFloat(0, 100)
    animator.setDuration(100)
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.alpha = 1 - (pos / 100)
        windowManager.updateViewLayout(ContentView.this, params)
      }
    })
    animator.addListener(new Animator.AnimatorListener {
      def onAnimationStart(animation: Animator) {
      }
      def onAnimationEnd(animation: Animator) {
        hide()
        params.alpha = 1
        windowManager.updateViewLayout(ContentView.this, params)
      }
      def onAnimationCancel(animation: Animator) {
      }
      def onAnimationRepeat(animation: Animator) {
      }
    })
    animator.start()
  }

  def changePositionIfIsNecessary(
      widthScreen: Int,
      heightScreen: Int,
      params: WindowManager.LayoutParams,
      windowManager: WindowManager): Unit = {
    if (params.x + getWidth > widthScreen) {
      params.x = widthScreen - getWidth
    }
    if (params.y + getHeight > heightScreen) {
      params.y = heightScreen - getHeight
    }
    windowManager.updateViewLayout(ContentView.this, params)
  }

}
