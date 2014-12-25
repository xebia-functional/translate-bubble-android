package com.fortysevendeg.translatebubble.ui.components

import android.animation.{Animator, AnimatorListenerAdapter}
import android.content.Context
import android.util.AttributeSet
import android.view.View._
import android.view.{View, ViewGroup}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.LayoutBuildingExtra._
import com.fortysevendeg.macroid.extras.RootView
import com.fortysevendeg.translatebubble.R
import macroid.{Snail, AppContext}
import macroid.FullDsl._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Promise
import scala.util.Success

class ActionsView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit appContext: AppContext)
    extends FrameLayout(context, attrs, defStyleAttr) {

  def this(context: Context)(implicit appContext: AppContext) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit appContext: AppContext) = this(context, attr, 0)

  val sizeClose = appContext.get.getResources.getDimension(R.dimen.size_close).toInt

  val marginClose = appContext.get.getResources.getDimension(R.dimen.margin_close).toInt

  implicit val rootView: RootView = new RootView(R.layout.actions_view)

  val closeView: Option[CloseView] = connect[CloseView](R.id.close)

  addView(rootView.view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

  def show() = {
    setVisibility(VISIBLE)
    runUi(closeView <~~ ActionsViewSnails.animIn)
  }

  def hide() = {
    runUi(closeView <~~ ActionsViewSnails.animOut(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        setVisibility(GONE)
      }
    }))
  }

  def isVisible(): Boolean = getVisibility == VISIBLE

  def isOverCloseView(x: Float, y: Float): Boolean = {
    val x2 = (getWidth / 2) + (sizeClose / 2)
    val y2 = getHeight - marginClose
    val x1 = (getWidth / 2) - (sizeClose / 2)
    val y1 = y2 - sizeClose
    (x > x1 && x < x2 && y > y1 && y < y2)
  }

  def getClosePosition() = closeView.map(
    view =>
      (getWidth / 2, (getHeight - marginClose - (view.getHeight / 2)))
  ).getOrElse(0, 0)

}

object ActionsViewSnails {

  val DISTANCE = 300

  val animIn = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.setTranslationY(DISTANCE)
      view.animate.translationY(0).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

  def animOut(listener: AnimatorListenerAdapter) = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.animate.translationY(DISTANCE).setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          listener.onAnimationEnd(animation)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

}
