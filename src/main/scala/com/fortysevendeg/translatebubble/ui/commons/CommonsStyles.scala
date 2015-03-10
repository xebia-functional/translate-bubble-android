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

package com.fortysevendeg.translatebubble.ui.commons

import android.support.v7.widget.{RecyclerView, Toolbar}
import android.view.Gravity
import android.widget._
import com.fortysevendeg.macroid.extras.FrameLayoutTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ViewGroupTweaks._
import com.fortysevendeg.translatebubble.R
import macroid.{AppContext, Tweak}

import scala.language.postfixOps

trait ToolbarStyles {

  def toolbarStyle(height: Int)(implicit appContext: AppContext): Tweak[Toolbar] =
    vContentSizeMatchWidth(height) +
        vBackground(R.color.primary)

}

trait ListStyles {

  def rootStyle(implicit appContext: AppContext): Tweak[FrameLayout] =
    vMatchParent

  def recyclerViewStyle(implicit appContext: AppContext): Tweak[RecyclerView] =
    vMatchParent +
        rvNoFixedSize +
        vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
        vgClipToPadding(false)

  val progressBarStyle: Tweak[ProgressBar] =
    vWrapContent +
        flLayoutGravity(Gravity.CENTER)
}

trait PlaceHolderStyles {

  val placeholderContentStyle: Tweak[LinearLayout] =
    vWrapContent +
        flLayoutGravity(Gravity.CENTER) +
        llGravity(Gravity.CENTER_HORIZONTAL) +
        llVertical +
        vGone

  val placeholderImageStyle: Tweak[ImageView] =
    vWrapContent

  def placeholderMessageStyle(implicit appContext: AppContext): Tweak[TextView] =
    vWrapContent +
        tvGravity(Gravity.CENTER) +
        tvColorResource(R.color.text_error_message) +
        tvSize(resGetInteger(R.integer.text_big)) +
        vPaddings(resGetDimensionPixelSize(R.dimen.padding_default_big))

  def placeholderButtonStyle(implicit appContext: AppContext): Tweak[TextView] =
    vWrapContent +
        vMinWidth(resGetDimensionPixelSize(R.dimen.width_button)) +
        tvText(R.string.reload) +
        tvColorResource(R.color.text_error_button) +
        vBackground(R.drawable.background_error_button) +
        tvAllCaps +
        tvSize(resGetInteger(R.integer.text_medium)) +
        tvGravity(Gravity.CENTER)

}