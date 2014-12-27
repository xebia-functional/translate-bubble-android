package com.fortysevendeg.translatebubble.ui.components

import android.animation.{Animator, AnimatorListenerAdapter}
import android.content.Context
import android.util.{Log, AttributeSet}
import android.view.View._
import android.view.animation.{AccelerateInterpolator, DecelerateInterpolator}
import android.view.{View, ViewGroup}
import android.widget.FrameLayout
import com.fortysevendeg.macroid.extras.LayoutBuildingExtra._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.ui.components.DisableViewTweaks._
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

  val options = List(DisableView.TYPE_15_MIN, DisableView.TYPE_2_HOURS, DisableView.TYPE_1_DAY, DisableView.TYPE_MANUALLY)

  var showingOptions = false

  val sizeClose = appContext.get.getResources.getDimension(R.dimen.size_close).toInt

  val marginClose = appContext.get.getResources.getDimension(R.dimen.margin_close).toInt

  val sizeDisable = appContext.get.getResources.getDimension(R.dimen.size_disable).toInt

  val marginTopDisable = appContext.get.getResources.getDimension(R.dimen.margin_top_disable).toInt

  val marginRightDisable = appContext.get.getResources.getDimension(R.dimen.margin_right_disable).toInt

  implicit val rootView: RootView = new RootView(R.layout.actions_view)

  val closeView: Option[CloseView] = connect[CloseView](R.id.close)

  val disableView: Option[DisableView] = connect[DisableView](R.id.disable)

  val disableManuallyView: Option[DisableView] = connect[DisableView](R.id.manually)

  val disable1DayView: Option[DisableView] = connect[DisableView](R.id.day_1)

  val disable2HoursView: Option[DisableView] = connect[DisableView](R.id.hour_2)

  val disable15MinView: Option[DisableView] = connect[DisableView](R.id.min_15)

  runUi(
    (disableManuallyView <~ vGone) ~ (disableManuallyView <~ dvTypeIcon(DisableView.TYPE_MANUALLY)) ~
        (disable1DayView <~ vGone) ~ (disable1DayView <~ dvTypeIcon(DisableView.TYPE_1_DAY)) ~
        (disable2HoursView <~ vGone) ~ (disable2HoursView <~ dvTypeIcon(DisableView.TYPE_2_HOURS)) ~
        (disable15MinView <~ vGone) ~ (disable15MinView <~ dvTypeIcon(DisableView.TYPE_15_MIN))
  )

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

  def getClosePosition() = closeView.map(
    view =>
      (getWidth / 2, (getHeight - marginClose - (view.getHeight / 2)))
  ).getOrElse(0, 0)

  def checkOptions(x: Float, y: Float) = {
    if (!showingOptions) {
      val isOverDisableView = disableView.map {
        view =>
          val x2 = getWidth - marginRightDisable
          val y2 = sizeDisable + marginTopDisable
          val x1 = x2 - sizeDisable
          val y1 = marginTopDisable
          (x > x1 && x < x2 && y > y1 && y < y2)
      }.getOrElse(false)
      if (isOverDisableView) {
        showOptions()
      }
    }
  }

  def isOverOption(x: Float, y: Float): Int = {
    val selectedList = options.map {
      t =>
        val x2 = getWidth - marginRightDisable - ((sizeDisable + marginRightDisable) * t)
        val y2 = sizeDisable + marginTopDisable
        val x1 = x2 - sizeDisable
        val y1 = marginTopDisable
        (t, (x > x1 && x < x2 && y > y1 && y < y2))
    }
    for (selected <- selectedList) {
      if (selected._2) return selected._1
    }
    0
  }

  def getOptionPosition(typeOption: Int) = {
    val x = getWidth - marginRightDisable - ((sizeDisable + marginRightDisable) * typeOption) - (sizeDisable / 2)
    val y = marginTopDisable + (sizeDisable / 2)
    (x, y)
  }

  def showOptions() = {
    showingOptions = true
    runUi(
      (disableManuallyView <~~ ActionsViewSnails.animDisableOptionIn) ~
          (disable1DayView <~~ ActionsViewSnails.animDisableOptionIn) ~
          (disable2HoursView <~~ ActionsViewSnails.animDisableOptionIn) ~
          (disable15MinView <~~ ActionsViewSnails.animDisableOptionIn)
    )
  }

  def hideOptions() = {
    showingOptions = false
    runUi(
      (disableManuallyView <~~ ActionsViewSnails.animDisableOptionOut) ~
          (disable1DayView <~~ ActionsViewSnails.animDisableOptionOut) ~
          (disable2HoursView <~~ ActionsViewSnails.animDisableOptionOut) ~
          (disable15MinView <~~ ActionsViewSnails.animDisableOptionOut)
    )
  }

  def reset() = {
    if (showingOptions) {
      hideOptions()
    }
  }

}

object ActionsViewSnails {

  val DISTANCE_OPTION = 50

  val DURATION_OPTION = 100

  val DISTANCE = 300

  val animDisableOptionIn = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.setVisibility(VISIBLE)
      view.setTranslationX(DISTANCE_OPTION)
      view.setAlpha(0)
      view.animate
          .setDuration(DURATION_OPTION)
          .translationX(0)
          .alpha(1)
          .setInterpolator(new DecelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

  val animDisableOptionOut = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.animate
          .setDuration(DURATION_OPTION)
          .translationX(DISTANCE_OPTION)
          .alpha(0)
          .setInterpolator(new AccelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setVisibility(GONE)
          animPromise.complete(Success(()))
        }
      })
          .start()
      animPromise.future
  }

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
