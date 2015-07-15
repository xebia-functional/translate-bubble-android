/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain
 *  a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.fortysevendeg.translatebubble.ui.commons

import android.support.v7.widget.{LinearLayoutManager, GridLayoutManager, RecyclerView}
import android.widget.{FrameLayout, LinearLayout, ProgressBar}
import com.fortysevendeg.macroid.extras.DeviceMediaQueries._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.translatebubble.R
import macroid.FullDsl._
import macroid.{ContextWrapper, Ui, ActivityContextWrapper}

trait ListLayout
  extends ListStyles
  with PlaceHolderLayout {

  var recyclerView = slot[RecyclerView]

  var progressBar = slot[ProgressBar]

  var placeholderContent = slot[LinearLayout]

  def layoutManager(implicit contextWrapper: ContextWrapper) = (tablet.b, landscape.b) match {
    case (true, true) =>
      new GridLayoutManager(contextWrapper.application, resGetInteger(R.integer.column_tablet_landscape))
    case (true, false) =>
      new GridLayoutManager(contextWrapper.application, resGetInteger(R.integer.column_or_tablet_or_landscape))
    case (false, true) =>
      new GridLayoutManager(contextWrapper.application, resGetInteger(R.integer.column_or_tablet_or_landscape))
    case _ => new LinearLayoutManager(contextWrapper.application)
  }


  def content(implicit contextWrapper: ActivityContextWrapper) = getUi(
    l[FrameLayout](
      w[ProgressBar] <~ wire(progressBar) <~ progressBarStyle,
      w[RecyclerView] <~ wire(recyclerView) <~ recyclerViewStyle,
      placeholder <~ wire(placeholderContent)
    ) <~ rootStyle
  )

  def initializeUi(implicit contextWrapper: ContextWrapper): Ui[_] =
    (recyclerView
      <~ rvLayoutManager(layoutManager)
      <~ rvAddItemDecoration(new HistoryItemDecorator))

  def loading(): Ui[_] =
    (progressBar <~ vVisible) ~
      (recyclerView <~ vGone) ~
      (placeholderContent <~ vGone)

  def failed(): Ui[_] = {
    loadFailed() ~
      (progressBar <~ vGone) ~
      (recyclerView <~ vGone) ~
      (placeholderContent <~ vVisible)
  }

  def empty(): Ui[_] = {
    loadEmpty() ~
      (progressBar <~ vGone) ~
      (recyclerView <~ vGone) ~
      (placeholderContent <~ vVisible)
  }

  def adapter[VH <: RecyclerView.ViewHolder](adapter: RecyclerView.Adapter[VH]): Ui[_] =
    (progressBar <~ vGone) ~
      (placeholderContent <~ vGone) ~
      (recyclerView <~ vVisible) ~
      (recyclerView <~ rvAdapter(adapter))

}