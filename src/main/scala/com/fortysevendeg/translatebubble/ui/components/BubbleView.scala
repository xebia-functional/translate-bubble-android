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

import android.animation.{Animator, AnimatorListenerAdapter, ValueAnimator}
import android.content.Context
import android.util.AttributeSet
import android.view.View._
import android.view.animation.{AccelerateInterpolator, Animation, LinearInterpolator, RotateAnimation}
import android.view.{View, WindowManager}
import android.widget.{FrameLayout, ImageView}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.ui.commons.Dimens
import macroid.FullDsl._
import macroid.{ServiceContextWrapper, Tweak, Ui}

class BubbleView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ServiceContextWrapper)
    extends FrameLayout(context, attrs, defStyleAttr) {

  val leftPosition = 0

  val rightPosition = 1

  val bubbleHorizontalDisplacement = contextWrapper.application.getResources.getDimension(R.dimen.bubble_horizontal_displacement).toInt

  def this(context: Context)(implicit contextWrapper: ServiceContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ServiceContextWrapper) = this(context, attr, 0)

  var positionBubble: Int = leftPosition

  var widthScreen: Int = 0

  var heightScreen: Int = 0

  val bubble = getUi(w[ImageView] <~ ivSrc(R.drawable.common_bubble))

  val loading = getUi(w[ImageView] <~ ivSrc(R.drawable.bubble_loading) <~ vInvisible)

  val anim: RotateAnimation = {
    val anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
    anim.setInterpolator(new LinearInterpolator)
    anim.setRepeatCount(Animation.INFINITE)
    anim.setDuration(1000)
    anim
  }

  runUi(this <~ vgAddViews(Seq(bubble, loading)))

  def init(paramsBubble: WindowManager.LayoutParams): Unit = {
    paramsBubble.x = left
    paramsBubble.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
  }

  def setDimensionsScreen(width: Int, height: Int): Unit = {
    widthScreen = width
    heightScreen = height
  }

  def changePositionIfIsNecessary(params: WindowManager.LayoutParams, windowManager: WindowManager): Ui[_] = {
    val previousHeight = heightScreen
    if (positionBubble == rightPosition || params.y > heightScreen) {
      params.x = right()
      params.y = (heightScreen * params.y) / previousHeight
      updateLayout(params, windowManager)
    } else Ui.nop
  }

  private[this] def left: Int = {
    positionBubble = leftPosition
    -bubbleHorizontalDisplacement
  }

  private[this] def right(): Int = {
    positionBubble = rightPosition
    widthScreen - getWidth + bubbleHorizontalDisplacement
  }

  private[this] def updateLayout(params: WindowManager.LayoutParams, windowManager: WindowManager) = Ui {
    windowManager.updateViewLayout(this, params)
  }

  def startAnimation(): Ui[_] = loading <~ vVisible <~ Tweak[View] (_.startAnimation(anim))

  def stopAnimation(): Ui[_] = loading <~ Tweak[View] (_.clearAnimation()) <~ vInvisible

  def show(params: WindowManager.LayoutParams, windowManager: WindowManager): Ui[_] = {
    val maybeUpdate = if (getVisibility != VISIBLE) {
      updateLayout(params, windowManager) ~ (this <~ vVisible)
    } else Ui.nop
    maybeUpdate ~ startAnimation()
  }

  def hide: Ui[_] = this <~ vGone

  def close(params: WindowManager.LayoutParams, windowManager: WindowManager): Ui[_] = hide ~ Ui {
    params.x = left
    params.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
  }

  def hideFromCloseAction(params: WindowManager.LayoutParams, windowManager: WindowManager): Ui[_] = Ui {
    val y: Int = params.y
    val to: Int = y + Dimens.distanceOut
    val animator: ValueAnimator = ValueAnimator.ofFloat(y, to)
    animator.setInterpolator(new AccelerateInterpolator())
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.y = pos.toInt
        runUi(updateLayout(params, windowManager))
      }
    })
    animator.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        runUi(hide)
        params.x = left
        params.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
      }
    })
    animator.start()
  }

  def hideFromOptionAction(params: WindowManager.LayoutParams, windowManager: WindowManager): Ui[_] = Ui {
    val x: Int = params.x
    val to: Int = x + Dimens.distanceOut
    val animator: ValueAnimator = ValueAnimator.ofFloat(x, to)
    animator.setInterpolator(new AccelerateInterpolator())
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.x = pos.toInt
        runUi(updateLayout(params, windowManager))
      }
    })
    animator.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        runUi(hide)
        params.x = left
        params.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
      }
    })
    animator.start()
  }

  def drop(params: WindowManager.LayoutParams, windowManager: WindowManager): Ui[_] = Ui {
    val x: Int = params.x
    val to: Int = if (x < widthScreen / 2) left else right()
    val animator: ValueAnimator = ValueAnimator.ofFloat(x, to)
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.x = pos.toInt
        runUi(updateLayout(params, windowManager))
      }
    })
    animator.start()
  }

}
