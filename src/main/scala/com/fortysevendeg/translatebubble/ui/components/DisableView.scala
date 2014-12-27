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

  var typeIcon = TYPE_DISABLE

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

    typeIcon match {
      case typeIcon if (typeIcon == TYPE_DISABLE) => {
        val sizeAcross = width / 6
        canvas.drawCircle(middleWidth, middleHeight, sizeAcross, paintStroke)
        canvas.drawLine(middleWidth + sizeAcross, middleHeight - sizeAcross, middleWidth - sizeAcross, middleHeight + sizeAcross, paintStroke)
      }
      case typeIcon if (typeIcon == TYPE_15_MIN) => {
        val text = "15m"
        canvas.drawText(text, middleWidth, baseline, paintWords)
      }
      case typeIcon if (typeIcon == TYPE_2_HOURS) => {
        val text = "2H"
        canvas.drawText(text, middleWidth, baseline, paintWords)
      }
      case typeIcon if (typeIcon == TYPE_1_DAY) => {
        val text = "1d"
        canvas.drawText(text, middleWidth, baseline, paintWords)
      }
      case typeIcon if (typeIcon == TYPE_MANUALLY) => {
        val text = "M"
        canvas.drawText(text, middleWidth, baseline, paintWords)
      }
    }

  }

}

object DisableView {
  val TYPE_DISABLE = 0
  val TYPE_15_MIN = 1
  val TYPE_2_HOURS = 2
  val TYPE_1_DAY = 3
  val TYPE_MANUALLY = 4
}

object DisableViewTweaks {
  type W = DisableView

  def dvTypeIcon(typeIcon: Int) = Tweak[W](_.typeIcon = typeIcon)

}
