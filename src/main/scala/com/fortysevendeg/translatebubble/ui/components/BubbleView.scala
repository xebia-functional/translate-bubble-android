package com.fortysevendeg.translatebubble.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View._
import android.view.WindowManager
import android.view.animation.{Animation, LinearInterpolator, RotateAnimation}
import android.widget.{FrameLayout, ImageView}
import com.fortysevendeg.translatebubble.R
import macroid.AppContext

class BubbleView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
    extends FrameLayout(context, attrs, defStyleAttr) {

  val POSITION_LEFT = 0

  val POSITION_RIGHT = 1

  val BUBBLE_HORIZONTAL_DISPLACEMENT = appContext.get.getResources.getDimension(R.dimen.bubble_horizontal_displacement).toInt

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  var positionBubble: Int = POSITION_LEFT

  var widthScreen: Int = 0

  var heightScreen: Int = 0

  val bubble: ImageView = {
    val bubble = new ImageView(getContext)
    bubble.setImageResource(R.drawable.bubble)
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
    if (positionBubble == POSITION_RIGHT || params.y > heightScreen) {
      params.x = right()
      params.y = (heightScreen * params.y) / previousHeight
      windowManager.updateViewLayout(BubbleView.this, params)
    }
  }

  def left: Int = {
    positionBubble = POSITION_LEFT
    -BUBBLE_HORIZONTAL_DISPLACEMENT
  }

  def right(): Int = {
    positionBubble = POSITION_RIGHT
    widthScreen - getWidth + BUBBLE_HORIZONTAL_DISPLACEMENT
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
