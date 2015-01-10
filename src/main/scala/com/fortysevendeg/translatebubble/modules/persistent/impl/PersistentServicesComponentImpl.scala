package com.fortysevendeg.translatebubble.modules.persistent.impl

import android.app.{AlarmManager, PendingIntent}
import android.content.{Context, Intent, SharedPreferences}
import android.preference.PreferenceManager
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.persistent._
import com.fortysevendeg.translatebubble.service.Service
import com.fortysevendeg.translatebubble.services.RestartTranslationService
import com.fortysevendeg.translatebubble.utils.TranslateUIType.TypeTranslateUI
import com.fortysevendeg.translatebubble.utils.{LanguageType, TranslateUIType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PersistentServicesComponentImpl
    extends PersistentServicesComponent {

  self: AppContextProvider =>

  val translationEnableKey = "translationEnable"

  val headUpNotificationKey = "headUpNotification"

  val wizardWasSeenKey = "wizardWasSeen"

  val fromLanguageKey = "fromLanguage"

  val toLanguageKey = "toLanguage"

  val typeNotificationKey = "typeNotification"

  val englishKey = "ENGLISH"

  val spanishKey = "SPANISH"

  lazy val persistentServices = new PersistentServicesImpl

  class PersistentServicesImpl
      extends PersistentServices {

    val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContextProvider.get)

    def enableTranslation(): Unit = sharedPreferences.edit().putBoolean(translationEnableKey, true).commit()

    def disableTranslation(): Unit = sharedPreferences.edit().putBoolean(translationEnableKey, false).commit()

    def disable30MinutesTranslation(): Unit = {
      sharedPreferences.edit().putBoolean(translationEnableKey, false).commit()
      val am = appContextProvider.get.getSystemService(Context.ALARM_SERVICE).asInstanceOf[AlarmManager]
      val i = new Intent(appContextProvider.get, classOf[RestartTranslationService])
      val pendingIntent = PendingIntent.getService(appContextProvider.get, 0, i, 0)
      am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * 30), pendingIntent)
    }

    override def isTranslationEnable(): Boolean = sharedPreferences.getBoolean(translationEnableKey, true)

    override def isHeadsUpEnable(): Boolean = sharedPreferences.getBoolean(headUpNotificationKey, true)

    override def isWizardWasSeen(): Boolean = sharedPreferences.getBoolean(wizardWasSeenKey, false)

    override def wizardWasSeen(): Unit = sharedPreferences.edit().putBoolean(wizardWasSeenKey, true).commit()

    override def getLanguages: Service[GetLanguagesRequest, GetLanguagesResponse] = request =>
      Future {
        GetLanguagesResponse(
          from = LanguageType.withName(sharedPreferences.getString(fromLanguageKey, englishKey)),
          to = LanguageType.withName(sharedPreferences.getString(toLanguageKey, spanishKey)))
      }

    override def getLanguagesString: Option[String] = {
      Some(appContextProvider.get.getString(R.string.toLanguages,
        getString(sharedPreferences.getString(fromLanguageKey, englishKey)),
        getString(sharedPreferences.getString(toLanguageKey, spanishKey))))
    }

    override def getTypeTranslateUI(): TypeTranslateUI = sharedPreferences match {
      case s: SharedPreferences if s.getBoolean(typeNotificationKey, false) => TranslateUIType.NOTIFICATION
      case _ => TranslateUIType.BUBBLE
    }

    def getString(res: String) = {
      // TODO Add this to macroid-extras
      val id = appContextProvider.get.getResources.getIdentifier(res, "string", appContextProvider.get.getPackageName)
      (if (id == 0) res else appContextProvider.get.getString(id))
    }

  }

}
