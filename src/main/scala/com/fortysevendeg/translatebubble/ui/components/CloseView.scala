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

  val paintCircle: Paint = {
    val paintCircle = new Paint
    paintCircle.setColor(Color.WHITE)
    paintCircle.setStyle(Paint.Style.STROKE)
    paintCircle.setStrokeWidth(stroke)
    paintCircle.setAntiAlias(true)
    paintCircle
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
    canvas.drawCircle(middleWidth, middleHeight, (width / 2) - stroke, paintCircle)
    val sizeAcross = width / 4
    canvas.drawLine(middleWidth - sizeAcross, middleHeight - sizeAcross, middleWidth + sizeAcross, middleHeight + sizeAcross, paintCircle)
    canvas.drawLine(middleWidth + sizeAcross, middleHeight - sizeAcross, middleWidth - sizeAcross, middleHeight + sizeAcross, paintCircle)
  }

}
