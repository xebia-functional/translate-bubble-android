package com.fortysevendeg.translatebubble.ui.components

import android.animation.{Animator, AnimatorListenerAdapter}
import android.content.Context
import android.util.AttributeSet
import android.view.View._
import android.view.animation.{AccelerateInterpolator, DecelerateInterpolator}
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

  val sizeDisable = appContext.get.getResources.getDimension(R.dimen.size_disable).toInt

  val marginDisable = appContext.get.getResources.getDimension(R.dimen.margin_disable).toInt

  implicit val rootView: RootView = new RootView(R.layout.actions_view)

  val closeView: Option[CloseView] = connect[CloseView](R.id.close)

  val disableView: Option[DisableView] = connect[DisableView](R.id.disable)

  addView(rootView.view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

  def show() = {
    setVisibility(VISIBLE)
    runUi(
      (closeView <~~ ActionsViewSnails.animCloseIn) ~
          (disableView <~~ ActionsViewSnails.animDisableIn)
    )
  }

  def hide() = {
    runUi(
      (closeView <~~ ActionsViewSnails.animCloseOut(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator): Unit = {
          super.onAnimationEnd(animation)
          setVisibility(GONE)
        }
      })) ~
          (disableView <~~ ActionsViewSnails.animDisableOut)
    )
  }

  def isVisible(): Boolean = getVisibility == VISIBLE

  def isOverCloseView(x: Float, y: Float): Boolean = {
    val x2 = (getWidth / 2) + (sizeClose / 2)
    val y2 = getHeight - marginClose
    val x1 = (getWidth / 2) - (sizeClose / 2)
    val y1 = y2 - sizeClose
    (x > x1 && x < x2 && y > y1 && y < y2)
  }

  def isOverDisableView(x: Float, y: Float): Boolean = {
    val x2 = getWidth - marginDisable
    val y2 = sizeDisable + marginDisable
    val x1 = x2 - sizeDisable
    val y1 = marginDisable
    (x > x1 && x < x2 && y > y1 && y < y2)
  }

  def getClosePosition() = closeView.map(
    view =>
      (getWidth / 2, (getHeight - marginClose - (view.getHeight / 2)))
  ).getOrElse(0, 0)

  def getDisablePosition() = disableView.map(
    view =>
      (getWidth - marginDisable - (view.getWidth / 2), marginDisable + (view.getHeight / 2))
  ).getOrElse(0, 0)

}

object ActionsViewSnails {

  val DISTANCE = 300

  val animDisableIn = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.setTranslationX(DISTANCE)
      view.animate
          .translationX(0)
          .setInterpolator(new DecelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

  val animDisableOut = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.animate
          .translationX(DISTANCE)
          .setInterpolator(new AccelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.complete(Success(()))
        }
      })
          .start()
      animPromise.future
  }

  val animCloseIn = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.setTranslationY(DISTANCE)
      view.animate
          .translationY(0)
          .setInterpolator(new DecelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

  def animCloseOut(listener: AnimatorListenerAdapter) = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.animate
          .translationY(DISTANCE)
          .setInterpolator(new AccelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          listener.onAnimationEnd(animation)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

}
