package com.fortysevendeg.translatebubble.ui.components

import android.animation.{Animator, ValueAnimator}
import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.View._
import android.view._
import android.widget.{FrameLayout, ImageView, LinearLayout, TextView}
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.macroid.ImageViewTweaks._
import com.fortysevendeg.translatebubble.macroid.LayoutBuildingExtra._
import com.fortysevendeg.translatebubble.macroid.RootView
import com.fortysevendeg.translatebubble.macroid.ViewTweaks._
import macroid.FullDsl._
import macroid.{AppContext, Ui}

class ContentView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
    extends FrameLayout(context, attrs, defStyleAttr)
    with GestureDetector.OnGestureListener {

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  private val SENSIBILITY: Float = 80

  implicit val rootView: RootView = new RootView(R.layout.content_view)

  private var showingInfo: Boolean = true
  private var gestureListener: GestureListener = null

  val detector: GestureDetectorCompat = new GestureDetectorCompat(appContext.get, this)

  val original: Option[TextView] = connect[TextView](R.id.original)
  val translate: Option[TextView] = connect[TextView](R.id.translate)
  val collapse: Option[TextView] = connect[TextView](R.id.collapse)
  val close: Option[TextView] = connect[TextView](R.id.close)
  val options: Option[ImageView] = connect[ImageView](R.id.options)
  val info: Option[LinearLayout] = connect[LinearLayout](R.id.info_layout)
  val buttons: Option[LinearLayout] = connect[LinearLayout](R.id.buttons_layout)

  options <~ On.click {
    Ui(if (showingInfo) showButtons() else showInfo())
  }

  showInfo()
  addView(rootView.view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

  def setGestureListener(gestureListener: GestureListener) {
    this.gestureListener = gestureListener
  }

  def setTexts(textOriginal: String, textTranslate: String) {
    runUi(
      (original <~ text(textOriginal)) ~ (translate <~ text(textTranslate))
    )
  }

  def setListeners(collapseClickListener: View.OnClickListener, closeClickListener: View.OnClickListener) {
    collapse.map(_.setOnClickListener(collapseClickListener))
    close.map(_.setOnClickListener(closeClickListener))
  }

  private def showInfo() {
    showingInfo = true
    runUi((info <~ vVisible) ~ (buttons <~ vGone) ~ (options <~ ivSrc(R.drawable.icon_options_light)))
  }

  private def showButtons() {
    showingInfo = false
    runUi((info <~ vGone) ~ (buttons <~ vVisible) ~ (options <~ ivSrc(R.drawable.icon_options_selected_light)))
  }

  def show() {
    showInfo()
    setVisibility(VISIBLE)
  }

  def hide() {
    setVisibility(INVISIBLE)
  }

  def collapse(params: WindowManager.LayoutParams, windowManager: WindowManager) {
    // TODO we can create a Snail when we have Macroid-Extra module
    val animator: ValueAnimator = ValueAnimator.ofFloat(params.y, params.y + 100)
    animator.setDuration(100)
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener {
      def onAnimationUpdate(animation: ValueAnimator) {
        val pos: Float = animation.getAnimatedValue.asInstanceOf[Float]
        params.alpha = 1 - (pos / 100)
        params.y = pos.toInt
        windowManager.updateViewLayout(ContentView.this, params)
      }
    })
    animator.addListener(new Animator.AnimatorListener {
      def onAnimationStart(animation: Animator) {
      }
      def onAnimationEnd(animation: Animator) {
        hide()
        setAlpha(1)
        params.y = 0
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

  override def onTouchEvent(event: MotionEvent): Boolean = {
    detector.onTouchEvent(event)
    super.onTouchEvent(event)
  }

  def onDown(e: MotionEvent): Boolean = true

  def onShowPress(e: MotionEvent) = ()

  def onSingleTapUp(e: MotionEvent): Boolean = true

  def onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean = true

  def onLongPress(e: MotionEvent) {
  }

  def onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean = {
    if (gestureListener != null) {
      if ((e1.getY - e2.getY) > SENSIBILITY) {
        gestureListener.onUp()
      }
      else if ((e2.getY - e1.getY) > SENSIBILITY) {
        gestureListener.onDown()
      }
      else if ((e1.getX - e2.getX) > SENSIBILITY) {
        gestureListener.onNext()
      }
      else if ((e2.getX - e1.getX) > SENSIBILITY) {
        gestureListener.onPrevious()
      }
    }
    true
  }

}

trait GestureListener {
  def onUp()
  def onDown()
  def onPrevious()
  def onNext()
}