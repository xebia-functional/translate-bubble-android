package com.fortysevendeg.translatebubble.modules.persistent.impl

import android.app.{PendingIntent, AlarmManager}
import android.content.{Intent, Context, SharedPreferences}
import android.preference.PreferenceManager
import android.util.Log
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.persistent.{GetLanguagesResponse, GetLanguagesRequest, PersistentServices, PersistentServicesComponent}
import com.fortysevendeg.translatebubble.service.Service
import com.fortysevendeg.translatebubble.utils.{LanguageType, TranslateUIType}
import com.fortysevendeg.translatebubble.utils.TranslateUIType.TypeTranslateUI
import scala.concurrent.ExecutionContext.Implicits.global
import com.fortysevendeg.translatebubble.services.RestartTranslationService

import scala.concurrent.Future

trait PersistentServicesComponentImpl
    extends PersistentServicesComponent {

  self : AppContextProvider =>

  lazy val persistentServices = new PersistentServicesImpl

  class PersistentServicesImpl
      extends PersistentServices {

    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContextProvider.get)

    def enableTranslation(): Unit = sharedPreferences.edit().putBoolean("translationEnable", true).commit()

    def disableTranslation(): Unit = sharedPreferences.edit().putBoolean("translationEnable", false).commit()

    def disable30MinutesTranslation(): Unit = {
      Log.d("TranslateApp", "disabling")
      sharedPreferences.edit().putBoolean("translationEnable", false).commit()
      val am = appContextProvider.get.getSystemService(Context.ALARM_SERVICE).asInstanceOf[AlarmManager]
      val i = new Intent(appContextProvider.get, classOf[RestartTranslationService])
      val pendingIntent = PendingIntent.getService(appContextProvider.get, 0, i, 0)
      am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * 30), pendingIntent)
    }

    override def isTranslationEnable(): Boolean = sharedPreferences.getBoolean("translationEnable", true)

    override def isHeadsUpEnable(): Boolean = sharedPreferences.getBoolean("headUpNotification", true)

    override def getLanguages: Service[GetLanguagesRequest, GetLanguagesResponse] = request =>
      Future {
        GetLanguagesResponse(
          from = LanguageType.withName(sharedPreferences.getString("fromLanguage", "ENGLISH")),
          to = LanguageType.withName(sharedPreferences.getString("toLanguage", "SPANISH")))
      }

    override def getTypeTranslateUI(): TypeTranslateUI = sharedPreferences match {
      case s: SharedPreferences if s.getBoolean("typeNotification", false) => TranslateUIType.NOTIFICATION
      case s: SharedPreferences if s.getBoolean("typeWatch", false) => TranslateUIType.WATCH
      case _ => TranslateUIType.BUBBLE
    }

  }

}
