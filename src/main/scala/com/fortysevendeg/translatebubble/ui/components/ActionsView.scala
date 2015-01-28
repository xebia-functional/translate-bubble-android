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
import android.util.{Log, AttributeSet}
import android.view.View._
import android.view.animation.{AccelerateInterpolator, DecelerateInterpolator}
import android.view.{View, ViewGroup}
import android.widget.{LinearLayout, FrameLayout}
import com.fortysevendeg.macroid.extras.LayoutBuildingExtra._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.ui.commons.Dimens
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

  val sizeClose = appContext.get.getResources.getDimension(R.dimen.size_close).toInt

  val marginClose = appContext.get.getResources.getDimension(R.dimen.margin_close).toInt

  val sizeDisable = appContext.get.getResources.getDimension(R.dimen.size_disable).toInt

  val marginTopDisable = appContext.get.getResources.getDimension(R.dimen.margin_top_disable).toInt

  val marginSeparateOptionsDisable = appContext.get.getResources.getDimension(R.dimen.margin_separate_options_disable).toInt

  implicit val rootView: RootView = new RootView(R.layout.actions_view)

  val closeView: Option[CloseView] = connect[CloseView](R.id.close)

  val disableView: Option[DisableView] = connect[DisableView](R.id.disable)

  val disableContentOptionsView: Option[LinearLayout] = connect[LinearLayout](R.id.content_options)

  val disable30MinView: Option[DisableView] = connect[DisableView](R.id.min_15)

  runUi(disable30MinView <~ dvTypeIcon(DisableView.TYPE_30_MIN))

  addView(rootView.view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

  def show() = {
    setVisibility(VISIBLE)
    runUi(
      (closeView <~~ ActionsViewSnails.animCloseIn) ~
          (disableContentOptionsView <~~ ActionsViewSnails.animDisableIn)
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
          (disableContentOptionsView <~~ ActionsViewSnails.animDisableOut)
    )
  }

  def isVisible: Boolean = getVisibility == VISIBLE

  def isOverCloseView(x: Float, y: Float): Boolean = {
    val x2 = (getWidth / 2) + (sizeClose / 2)
    val y2 = getHeight - marginClose
    val x1 = (getWidth / 2) - (sizeClose / 2)
    val y1 = y2 - sizeClose
    x > x1 && x < x2 && y > y1 && y < y2
  }

  def getClosePosition = (getWidth / 2, getHeight - marginClose - (sizeClose / 2))

  def isOverDisableView(x: Float, y: Float): Boolean = {
    val x1 = getWidth - sizeDisable - marginSeparateOptionsDisable
    val y1 = marginTopDisable
    val x2 = x1 + sizeDisable
    val y2 = y1 + sizeDisable
    x > x1 && x < x2 && y > y1 && y < y2
  }

  def getDisablePosition =
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
          animPromise.complete(Success(()))
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
          animPromise.complete(Success(()))
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
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

}
