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