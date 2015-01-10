package com.fortysevendeg.translatebubble.ui.components

import android.content.Context
import android.graphics._
import android.util.AttributeSet
import android.view.View
import com.fortysevendeg.translatebubble.R

class CloseView(context: Context, attrs: AttributeSet, defStyleAttr: Int)
    extends View(context, attrs, defStyleAttr) {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val stroke: Int = context.getResources.getDimension(R.dimen.stroke_close).toInt

  var width: Int = 0
  var height: Int = 0
  var middleWidth: Int = 0
  var middleHeight: Int = 0

  val paintStroke: Paint = {
    val paint = new Paint
    paint.setColor(Color.WHITE)
    paint.setStyle(Paint.Style.STROKE)
    paint.setStrokeWidth(stroke)
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
    }
  }

  protected override def onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    load()
    canvas.drawCircle(middleWidth, middleHeight, (width / 2) - stroke, paintFillCircle)
    canvas.drawCircle(middleWidth, middleHeight, (width / 2) - stroke, paintStroke)
    val sizeAcross = width / 6
    canvas.drawLine(middleWidth - sizeAcross, middleHeight - sizeAcross, middleWidth + sizeAcross, middleHeight + sizeAcross, paintStroke)
    canvas.drawLine(middleWidth + sizeAcross, middleHeight - sizeAcross, middleWidth - sizeAcross, middleHeight + sizeAcross, paintStroke)
  }

}
