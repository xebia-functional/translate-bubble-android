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

package com.fortysevendeg.translatebubble.ui.wizard

import android.view.Gravity
import android.widget.LinearLayout
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.translatebubble.R
import macroid.AppContext
import macroid.FullDsl._
import scala.language.postfixOps

object Styles {

  def rootStyle(implicit appContext: AppContext) = vMatchParent + llVertical +
      vBackgroundColor(appContext.get.getResources.getColor(R.color.wizard_background))

  val pagerStyle = vMatchParent + llMatchWeightVertical

  def bottomContentStyle(implicit appContext: AppContext) = vContentSizeMatchWidth(60 dp)

  def paginationContentStyle(implicit appContext: AppContext) = vMatchParent +
      llHorizontal + llGravity(Gravity.CENTER)

  def paginationItemStyle(implicit appContext: AppContext) = lp[LinearLayout](16 dp, 16 dp) +
      vPaddings(4 dp) + ivSrc(R.drawable.wizard_pager)

  def gotItStyle(implicit appContext: AppContext) = vMatchParent +
      tvSize(14) + tvText(R.string.gotIt) + tvColor(appContext.get.getResources.getColor(R.color.wizard_button)) +
      vBackground(R.drawable.wizard_background_got_it) + vGone

  def contentStepsStyle(implicit appContext: AppContext) = vMatchParent + llVertical +
      vPaddings(8 dp) + llGravity(Gravity.CENTER)

  def placeHolderStyle(implicit appContext: AppContext) = vWrapContent + vPadding(8 dp, 8 dp, 8 dp, 24 dp)

  def titleStepStyle(implicit appContext: AppContext) = vWrapContent + vPadding(24 dp, 8 dp, 24 dp, 8 dp) +
      tvSize(29) + tvColor(appContext.get.getResources.getColor(R.color.wizard_title))

  def descriptionStepStyle(implicit appContext: AppContext) = vWrapContent + vPadding(24 dp, 8 dp, 24 dp, 8 dp) +
      tvSize(17) + tvColor(appContext.get.getResources.getColor(R.color.wizard_description)) +
      tvGravity(Gravity.CENTER_HORIZONTAL)

}
