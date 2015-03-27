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

import android.graphics.{Rect, Canvas}
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import android.view.View
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.translatebubble.R
import macroid.AppContext

class HistoryItemDecorator(implicit appContext: AppContext)
  extends RecyclerView.ItemDecoration {

  override def getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
    outRect.left = resGetDimensionPixelSize(R.dimen.padding_default)
    outRect.right = resGetDimensionPixelSize(R.dimen.padding_default)
    outRect.bottom = resGetDimensionPixelSize(R.dimen.padding_default)
    outRect.top = resGetDimensionPixelSize(R.dimen.padding_default)
  }
}