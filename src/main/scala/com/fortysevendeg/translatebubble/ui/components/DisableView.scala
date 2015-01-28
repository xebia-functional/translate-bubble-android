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

import android.content.Context
import android.graphics._
import android.util.{TypedValue, AttributeSet}
import android.view.View
import com.fortysevendeg.translatebubble.R
import macroid.Tweak

class DisableView(context: Context, attrs: AttributeSet, defStyleAttr: Int)
    extends View(context, attrs, defStyleAttr) {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val stroke: Int = context.getResources.getDimension(R.dimen.stroke_close).toInt

  var width: Int = 0
  var height: Int = 0
  var middleWidth: Int = 0
  var middleHeight: Int = 0
  var baseline: Int = 0

  import com.fortysevendeg.translatebubble.ui.components.DisableView._

  var iconType = TYPE_DISABLE

  val paintStroke: Paint = {
    val paint = new Paint
    paint.setColor(Color.WHITE)
    paint.setStyle(Paint.Style.STROKE)
    paint.setStrokeWidth(stroke)
    paint.setAntiAlias(true)
    paint
  }

  val paintWords: Paint = {
    val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources.getDisplayMetrics)
    val paint = new Paint
    paint.setTextAlign(Paint.Align.CENTER)
    paint.setTextSize(size)
    paint.setColor(Color.WHITE)
    paint.setAntiAlias(true)
    paint
  }

  val paintFillCircle: Paint = {
    val paint = new Paint
    paint.setColor(Color.BLACK)
    paint.setStyle(Paint.Style.FILL)
    paint.setAntiAlias(true)
    paint.setAlpha(50)
    paint
  }

  private def load() {
    if (width == 0) {
      width = getWidth
      height = getHeight
      middleWidth = width / 2
      middleHeight = height / 2
      baseline = (height * 0.63).toInt
    }
  }

  protected override def onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    load()
    canvas.drawCircle(middleWidth, middleHeight, (width / 2) - stroke, paintFillCircle)
    canvas.drawCircle(middleWidth, middleHeight, (width / 2) - stroke, paintStroke)

    iconType match {
      case TYPE_DISABLE => {
        val sizeAcross = width / 6
        canvas.drawCircle(middleWidth, middleHeight, sizeAcross, paintStroke)
        canvas.drawLine(middleWidth + sizeAcross, middleHeight - sizeAcross, middleWidth - sizeAcross, middleHeight + sizeAcross, paintStroke)
      }
      case TYPE_30_MIN => canvas.drawText("30m", middleWidth, baseline, paintWords)
      case TYPE_MANUALLY => canvas.drawText("M", middleWidth, baseline, paintWords)
    }

  }

}

object DisableView {
  val TYPE_DISABLE = 0
  val TYPE_30_MIN = 1
  val TYPE_MANUALLY = 4
}

object DisableViewTweaks {
  type W = DisableView

  def dvTypeIcon(iconType: Int) = Tweak[W](_.iconType = iconType)

}
