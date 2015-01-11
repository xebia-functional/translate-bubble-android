package com.fortysevendeg.translatebubble.utils

import macroid.AppContext

object TranslateUIType extends Enumeration {
  type TypeTranslateUI = Value
  val NOTIFICATION, BUBBLE = Value

  def stringNames(): List[String] = {
    var result: List[String] = List.empty
    for (value <- values) {
      result = value.toString :: result
    }
    result
  }

  def resourceNames(implicit appContext: AppContext): List[String] = {
    var result: List[String] = List.empty
    for (value <- values) {
      val id = appContext.get.getResources.getIdentifier(value.toString, "string", appContext.get.getPackageName)
      result = (if (id == 0) value.toString else appContext.get.getString(id)) :: result
    }
    result
  }

}

