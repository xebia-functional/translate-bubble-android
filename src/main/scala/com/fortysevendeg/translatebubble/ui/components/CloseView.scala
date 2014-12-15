package com.fortysevendeg.translatebubble.ui.components

import android.content.Context
import android.graphics._
import android.util.AttributeSet
import android.view.View
import android.view.View._
import com.fortysevendeg.translatebubble.R

class CloseView(context: Context, attrs: AttributeSet, defStyleAttr: Int)
    extends View(context, attrs, defStyleAttr) {

  def this(context: Context) = this(context, null, 0)

  def this(context: Context, attr: AttributeSet) = this(context, attr, 0)

  val paintBackground: Paint = new Paint

  var width: Int = 0
  var height: Int = 0
  var middleWidth: Int = 0
  var middleHeight: Int = 0

  val positionsColors: Array[Float] = Array[Float](0f, 1f)

  val colors: Array[Int] = Array[Int](Color.parseColor("#99000000"), Color.TRANSPARENT)

  val radius: Int = context.getResources.getDimension(R.dimen.radius_close).toInt

  val sizeAcross: Int = radius / 3

  val paintCircle: Paint = {
    val stroke: Int = context.getResources.getDimension(R.dimen.stroke_close).toInt
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
      paintBackground.setShader(new LinearGradient(width / 2, height, width / 2, 0, colors, positionsColors, Shader.TileMode.CLAMP))
    }
  }

  protected override def onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    load()
    canvas.drawRect(0, 0, width, height, paintBackground)
    canvas.drawCircle(middleWidth, middleHeight, radius, paintCircle)
    canvas.drawLine(middleWidth - sizeAcross, middleHeight - sizeAcross, middleWidth + sizeAcross, middleHeight + sizeAcross, paintCircle)
    canvas.drawLine(middleWidth + sizeAcross, middleHeight - sizeAcross, middleWidth - sizeAcross, middleHeight + sizeAcross, paintCircle)
  }

  def show() = setVisibility(VISIBLE)

  def hide() = setVisibility(GONE)

  def isVisible: Boolean = getVisibility == VISIBLE

}
