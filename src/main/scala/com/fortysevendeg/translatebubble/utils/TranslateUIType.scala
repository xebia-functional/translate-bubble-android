package com.fortysevendeg.translatebubble.utils

import macroid.AppContext

object TranslateUIType extends Enumeration {
  type TypeTranslateUI = Value
  val NOTIFICATION, BUBBLE = Value

  def stringNames(): List[String] = {
    TranslateUIType.values.map(_.toString).toList
  }

  def resourceNames(implicit appContext: AppContext): List[String] = {
    TranslateUIType.values.map {
      v =>
        val id = appContext.get.getResources.getIdentifier(v.toString, "string", appContext.get.getPackageName)
        if (id == 0) v.toString else appContext.get.getString(id)
    }.toList
  }

}

