package com.fortysevendeg.translatebubble.modules.persistent.impl

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.fortysevendeg.translatebubble.macroid.AppContextProvider
import com.fortysevendeg.translatebubble.modules.persistent.{GetLanguagesResponse, GetLanguagesRequest, PersistentServices, PersistentServicesComponent}
import com.fortysevendeg.translatebubble.service.Service
import com.fortysevendeg.translatebubble.utils.{TypeLanguage, TypeTranslateUI}
import com.fortysevendeg.translatebubble.utils.TypeTranslateUI.TypeTranslateUI
import macroid.AppContext
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait PersistentServicesComponentImpl
    extends PersistentServicesComponent {

  self : AppContextProvider =>

  def persistentServices = new PersistentServicesImpl

  class PersistentServicesImpl
      extends PersistentServices {

    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContextProvider.get)

    override def isTranslationEnable(): Boolean = {
      sharedPreferences.getBoolean("translationEnable", true)
    }

    override def isHeadsUpEnable(): Boolean = {
      sharedPreferences.getBoolean("headUpNotification", true)
    }

    override def getLanguages: Service[GetLanguagesRequest, GetLanguagesResponse] = {
      request =>
        Future {
          GetLanguagesResponse(
            from = TypeLanguage.withName(sharedPreferences.getString("fromLanguage", "ENGLISH")),
            to = TypeLanguage.withName(sharedPreferences.getString("toLanguage", "SPANISH")))
        }
    }

    override def getTypeTranslateUI(): TypeTranslateUI = {
      if (sharedPreferences.getBoolean("typeNotification", false)) {
        return TypeTranslateUI.NOTIFICATION
      } else if (sharedPreferences.getBoolean("typeWatch", false)) {
        return TypeTranslateUI.WATCH
      }
      return TypeTranslateUI.BUBBLE
    }

  }

}
