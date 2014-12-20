package com.fortysevendeg.translatebubble.macroid

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import macroid.{ActivityContext, Ui}

object ExtraUIActions {

  def uiStartActivity[T <: Activity]()(implicit c : ActivityContext, m : Manifest[T]): Ui[Unit] =
    Ui(c.get.startActivity(new Intent(c.get, m.runtimeClass)))

  def uiStartActivityForResult[T <: Activity](result: Int)(implicit c : ActivityContext, m : Manifest[T]): Ui[Unit] =
    Ui(c.get.startActivityForResult(new Intent(c.get, m.runtimeClass), result))

}

object ExtraActions {

  def aStartActivity[T <: Activity]()(implicit c : ActivityContext, m : Manifest[T]): Unit =
    c.get.startActivity(new Intent(c.get, m.runtimeClass))

  def aStartActivityForResult[T <: Activity](result: Int)(implicit c : ActivityContext, m : Manifest[T]): Unit =
    c.get.startActivityForResult(new Intent(c.get, m.runtimeClass), result)

  def aShortToast(msg: String)(implicit c : ActivityContext) =
    Toast.makeText(c.get, msg, Toast.LENGTH_SHORT).show()

  def aLongToast(msg: String)(implicit c : ActivityContext) =
    Toast.makeText(c.get, msg, Toast.LENGTH_LONG).show()

}
