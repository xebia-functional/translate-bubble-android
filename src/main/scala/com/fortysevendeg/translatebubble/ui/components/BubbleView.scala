package com.fortysevendeg.translatebubble.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.WindowManager
import android.view.animation.{LinearInterpolator, Animation, RotateAnimation}
import android.widget.{FrameLayout, ImageView}
import com.fortysevendeg.translatebubble.R
import android.view.View._
import macroid.AppContext

class BubbleView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
    extends FrameLayout(context, attrs, defStyleAttr) {

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

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

  var widthScreen: Int = 0
  var heightScreen: Int = 0

  val heightCloseZone: Int = appContext.get.getResources.getDimension(R.dimen.height_close_zone).toInt

  addView(bubble)
  addView(loading)

  def init(heightScreen: Int, widthScreen: Int) {
    this.widthScreen = widthScreen
    this.heightScreen = heightScreen
  }

  def left: Int = 0

  def right: Int = widthScreen

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

  def drop(params: WindowManager.LayoutParams, windowManager: WindowManager) {
    if (params.y > heightScreen - heightCloseZone) {
      hide()
      params.x = 0
      params.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
    } else {
      val x: Int = params.x
      val to: Int = if (x < widthScreen / 2) left else right
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

}
