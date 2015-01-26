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
import android.view.WindowManager
import android.view.animation.{AccelerateInterpolator, Animation, LinearInterpolator, RotateAnimation}
import android.widget.{FrameLayout, ImageView}
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.ui.commons.Dimens
import macroid.AppContext

class BubbleView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
    extends FrameLayout(context, attrs, defStyleAttr) {

  val leftPosition = 0

  val rightPosition = 1

  val bubbleHorizontalDisplacement = appContext.get.getResources.getDimension(R.dimen.bubble_horizontal_displacement).toInt

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  var positionBubble: Int = leftPosition

  var widthScreen: Int = 0

  var heightScreen: Int = 0

  val bubble: ImageView = {
    val bubble = new ImageView(getContext)
    bubble.setImageResource(R.drawable.common_bubble)
    bubble
  }

  val loading: ImageView = {
    val loading = new ImageView(getContext)
    loading.setImageResource(R.drawable.bubble_loading)
    loading.setVisibility(INVISIBLE)
    loading
  }

  val anim: RotateAnimation = {
    val anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
    anim.setInterpolator(new LinearInterpolator)
    anim.setRepeatCount(Animation.INFINITE)
    anim.setDuration(1000)
    anim
  }

  addView(bubble)
  addView(loading)

  def init(paramsBubble: WindowManager.LayoutParams) {
    paramsBubble.x = left
    paramsBubble.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
  }

  def setDimensionsScreen(width: Int, height: Int): Unit = {
    val previousHeight = heightScreen
    widthScreen = width
    heightScreen = height
  }

  def changePositionIfIsNecessary(params: WindowManager.LayoutParams, windowManager: WindowManager): Unit = {
    val previousHeight = heightScreen
    if (positionBubble == rightPosition || params.y > heightScreen) {
      params.x = right()
      params.y = (heightScreen * params.y) / previousHeight
      windowManager.updateViewLayout(BubbleView.this, params)
    }
  }

  def left: Int = {
    positionBubble = leftPosition
    -bubbleHorizontalDisplacement
  }

  def right(): Int = {
    positionBubble = rightPosition
    widthScreen - getWidth + bubbleHorizontalDisplacement
  }

  def startAnimation() {
    loading.setVisibility(VISIBLE)
    loading.startAnimation(anim)
  }

  def stopAnimation() {
    loading.clearAnimation()
    loading.setVisibility(INVISIBLE)
  }

  def show(params: WindowManager.LayoutParams, windowManager: WindowManager) {
    if (getVisibility != VISIBLE) {
      windowManager.updateViewLayout(this, params)
      setVisibility(VISIBLE)
    }
    loading.setVisibility(VISIBLE)
    loading.startAnimation(anim)
  }

  def hide() = setVisibility(GONE)

  def close(params: WindowManager.LayoutParams, windowManager: WindowManager) = {
    hide()
    params.x = left
    params.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
  }

  def hideFromCloseAction(params: WindowManager.LayoutParams, windowManager: WindowManager) = {
    val y: Int = params.y
    val to: Int = y + Dimens.distanceOut
    val animator: ValueAnimator = ValueAnimator.ofFloat(y, to)
    animator.setInterpolator(new AccelerateInterpolator())
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.y = pos.toInt
        windowManager.updateViewLayout(BubbleView.this, params)
      }
    })
    animator.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        hide()
        params.x = left
        params.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
      }
    })
    animator.start()
  }

  def hideFromOptionAction(params: WindowManager.LayoutParams, windowManager: WindowManager) = {
    val x: Int = params.x
    val to: Int = x + Dimens.distanceOut
    val animator: ValueAnimator = ValueAnimator.ofFloat(x, to)
    animator.setInterpolator(new AccelerateInterpolator())
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.x = pos.toInt
        windowManager.updateViewLayout(BubbleView.this, params)
      }
    })
    animator.addListener(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        hide()
        params.x = left
        params.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
      }
    })
    animator.start()
  }

  def drop(params: WindowManager.LayoutParams, windowManager: WindowManager) {
    val x: Int = params.x
    val to: Int = if (x < widthScreen / 2) left else right()
    val animator: ValueAnimator = ValueAnimator.ofFloat(x, to)
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.x = pos.toInt
        windowManager.updateViewLayout(BubbleView.this, params)
      }
    })
    animator.start()
  }

}
