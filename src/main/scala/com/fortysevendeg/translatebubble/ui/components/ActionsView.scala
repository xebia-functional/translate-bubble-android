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

import android.animation.{Animator, AnimatorListenerAdapter}
import android.content.Context
import android.util.AttributeSet
import android.view.View._
import android.view.animation.{AccelerateInterpolator, DecelerateInterpolator}
import android.view.{Gravity, View, ViewGroup}
import android.widget.{FrameLayout, LinearLayout}
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.ui.commons.Dimens
import com.fortysevendeg.translatebubble.ui.components.DisableViewTweaks._
import macroid.FullDsl._
import macroid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.Success

class ActionsView(context: Context, attrs: AttributeSet, defStyleAttr: Int)(implicit contextWrapper: ServiceContextWrapper)
    extends FrameLayout(context, attrs, defStyleAttr)
    with ActionViewLayout {

  def this(context: Context)(implicit contextWrapper: ServiceContextWrapper) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet)(implicit contextWrapper: ServiceContextWrapper) = this(context, attr, 0)

  val resources = contextWrapper.application.getResources

  val sizeClose = resGetDimensionPixelSize(R.dimen.size_close)

  val marginClose = resGetDimensionPixelSize(R.dimen.margin_close)

  val sizeDisable = resGetDimensionPixelSize(R.dimen.size_disable)

  val marginTopDisable = resGetDimensionPixelSize(R.dimen.margin_top_disable)

  val marginSeparateOptionsDisable = resGetDimensionPixelSize(R.dimen.margin_separate_options_disable)

  addView(layout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

  runUi(disable30MinView <~ dvTypeIcon(DisableView.TYPE_30_MIN))

  def show(): Ui[_] = if (!isVisible) { (this <~ vVisible) ~
      (closeView <~~ ActionsViewSnails.animCloseIn) ~
      (disableContentOptionsView <~~ ActionsViewSnails.animDisableIn)
  } else Ui.nop

  def hide(): Ui[_] = if (isVisible) {
    (closeView <~~ ActionsViewSnails.animCloseOut(new AnimatorListenerAdapter {
      override def onAnimationEnd(animation: Animator): Unit = {
        super.onAnimationEnd(animation)
        runUi(gone)
      }
    })) ~
        (disableContentOptionsView <~~ ActionsViewSnails.animDisableOut)
  } else Ui.nop

  def gone: Ui[_] = this <~ vGone

  def isVisible: Boolean = getVisibility == VISIBLE

  def isOverCloseView(x: Float, y: Float): Boolean = {
    val x2 = (getWidth / 2) + (sizeClose / 2)
    val y2 = getHeight - marginClose
    val x1 = (getWidth / 2) - (sizeClose / 2)
    val y1 = y2 - sizeClose
    x > x1 && x < x2 && y > y1 && y < y2
  }

  def getClosePosition: (Int, Int) = (getWidth / 2, getHeight - marginClose - (sizeClose / 2))

  def isOverDisableView(x: Float, y: Float): Boolean = {
    val x1 = getWidth - sizeDisable - marginSeparateOptionsDisable
    val y1 = marginTopDisable
    val x2 = x1 + sizeDisable
    val y2 = y1 + sizeDisable
    x > x1 && x < x2 && y > y1 && y < y2
  }

  def getDisablePosition: (Int, Int) =
    (getWidth - (sizeDisable / 2) - marginSeparateOptionsDisable, marginTopDisable + (sizeDisable / 2))

  def isOver30minView(x: Float, y: Float): Boolean = {
    val x1 = getWidth - sizeDisable - marginSeparateOptionsDisable
    val y1 = marginTopDisable + sizeDisable + marginSeparateOptionsDisable
    val x2 = x1 + sizeDisable
    val y2 = y1 + sizeDisable
    x > x1 && x < x2 && y > y1 && y < y2
  }

  def get30minPosition(): (Int, Int) =
    (getWidth - (sizeDisable / 2) - marginSeparateOptionsDisable,
      marginTopDisable + marginSeparateOptionsDisable + sizeDisable + (sizeDisable / 2))

}

object ActionsViewSnails {

  val animDisableIn = Snail[View] {
    view =>
      val animPromise = Promise[Unit]()
      view.setTranslationX(Dimens.distanceOut)
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
    view =>
      val animPromise = Promise[Unit]()
      view.animate
          .translationX(Dimens.distanceOut)
          .setInterpolator(new AccelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.success()
        }
      }).start()
      animPromise.future
  }

  val animCloseIn = Snail[View] {
    view =>
      val animPromise = Promise[Unit]()
      view.setTranslationY(Dimens.distanceOut)
      view.animate
          .translationY(0)
          .setInterpolator(new DecelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.success()
        }
      }).start()
      animPromise.future
  }

  def animCloseOut(listener: AnimatorListenerAdapter) = Snail[View] {
    view =>
      val animPromise = Promise[Unit]()
      view.animate
          .translationY(Dimens.distanceOut)
          .setInterpolator(new AccelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          listener.onAnimationEnd(animation)
          animPromise.success()
        }
      }).start()
      animPromise.future
  }

}

trait ActionViewLayout {

  var closeView = slot[CloseView]

  var disableView = slot[DisableView]

  var disableContentOptionsView = slot[LinearLayout]

  var disable30MinView = slot[DisableView]

  def layout(implicit contextWrapper: ServiceContextWrapper) = getUi(
    l[FrameLayout](
      l[LinearLayout](
        w[DisableView] <~ wire(disableView) <~ actionDisableButtonStyle,
        w[DisableView] <~ wire(disable30MinView) <~ action30mDisableButtonStyle
      ) <~ contentDisableButtonsStyle <~ wire(disableContentOptionsView),
      w[CloseView] <~ wire(closeView) <~ actionCloseButtonStyle
    ) <~ rootStyle
  )

  def rootStyle(implicit contextWrapper: ContextWrapper): Tweak[FrameLayout] =
    vMatchParent +
        vBackground(R.drawable.background_system_alert)

  def contentDisableButtonsStyle(implicit contextWrapper: ContextWrapper): Tweak[LinearLayout] =
    vWrapContent +
        llVertical +
        vPadding(paddingTop = resGetDimensionPixelSize(R.dimen.margin_top_disable)) +
        flLayoutGravity(Gravity.RIGHT | Gravity.TOP)

  def actionDisableButtonStyle(implicit contextWrapper: ContextWrapper): Tweak[DisableView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_disable)
    lp[ViewGroup](size, size) +
        llLayoutMargin(marginRight = resGetDimensionPixelSize(R.dimen.margin_separate_options_disable))
  }

  def action30mDisableButtonStyle(implicit contextWrapper: ContextWrapper): Tweak[DisableView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_disable)
    val margin = resGetDimensionPixelSize(R.dimen.margin_separate_options_disable)
    lp[ViewGroup](size, size) +
        llLayoutMargin(marginRight = margin, marginTop = margin)
  }

  def actionCloseButtonStyle(implicit contextWrapper: ContextWrapper): Tweak[CloseView] = {
    val size = resGetDimensionPixelSize(R.dimen.size_close)
    val margin = resGetDimensionPixelSize(R.dimen.margin_close)
    lp[FrameLayout](size, size) +
        Tweak[View] {
        view ⇒
          val params = new FrameLayout.LayoutParams(view.getLayoutParams)
          params.setMargins(margin, margin, margin, margin)
          params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
          view.setLayoutParams(params)
      }
  }

}