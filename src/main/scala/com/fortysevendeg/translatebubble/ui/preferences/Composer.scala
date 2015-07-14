package com.fortysevendeg.translatebubble.ui.preferences

import android.app.AlertDialog
import android.content.{DialogInterface, Intent}
import android.net.Uri
import android.os.Bundle
import android.preference.Preference.{OnPreferenceChangeListener, OnPreferenceClickListener}
import android.preference._
import com.fortysevendeg.macroid.extras.PreferencesBuildingExtra._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.RootPreferencesFragment
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.analytics.AnalyticsServicesComponent
import com.fortysevendeg.translatebubble.modules.persistent.{PersistentServicesComponent, GetLanguagesRequest}
import com.fortysevendeg.translatebubble.ui.commons.Strings._
import com.fortysevendeg.translatebubble.ui.history.TranslationHistoryActivity
import com.fortysevendeg.translatebubble.ui.wizard.WizardActivity
import com.fortysevendeg.translatebubble.utils.{Notification, LanguageType, TranslateUiType}
import macroid.{ContextWrapper, Ui}
import macroid.FullDsl._

trait Composer {
  self: PreferenceFragment
    with PersistentServicesComponent
    with AnalyticsServicesComponent =>

  type ClickListenerFunction = () => Boolean
  type ChangeListenerFunction = (String) => Boolean

  implicit lazy val rootPreferencesFragment = new RootPreferencesFragment(this, R.xml.preferences)

  protected lazy val typeTranslate = connect[ListPreference]("typeTranslate")
  protected lazy val headUpNotification = connect[CheckBoxPreference]("headUpNotification")
  protected lazy val toLanguage = connect[ListPreference]("toLanguage")
  protected lazy val fromLanguage = connect[ListPreference]("fromLanguage")
  protected lazy val openSource = connect[PreferenceScreen]("openSource")
  protected lazy val showTutorial = connect[PreferenceScreen]("showTutorial")
  protected lazy val showHistory = connect[PreferenceScreen]("showHistory")
  protected lazy val about = connect[PreferenceScreen]("about")

  def initializePreferences(implicit contextWrapper: ContextWrapper) = {
    showTutorial foreach (onPreferenceClickListener(_,
      () => {
        Option(getActivity) foreach {
          activity =>
            val intent = new Intent(activity, classOf[WizardActivity])
            val bundle = new Bundle()
            bundle.putBoolean(WizardActivity.keyModeTutorial, true)
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
        true
      }))

    showHistory foreach (onPreferenceClickListener(_,
      () => {
        Option(getActivity) foreach {
          activity =>
            val intent = new Intent(activity, classOf[TranslationHistoryActivity])
            activity.startActivity(intent)
        }
        true
      }))

    openSource foreach (onPreferenceClickListener(_,
      () => {
        analyticsServices.send(analyticsOpenSourceDialog)
        showOpenSourceDialog
        true
      }))

    about foreach (onPreferenceClickListener(_,
      () => {
        analyticsServices.send(analyticsOpenSourceDialog)
        showAboutDialog
        true
      }))

    typeTranslate foreach {
      translate =>
        runUi(setTypeTranslated(translate.getValue))
        val translateSortedTuples = TranslateUiType.toSortedTuples()
        val translates: List[String] = translateSortedTuples map (_._2)
        val translatesValues: List[String] = translateSortedTuples map (_._1)
        translate.setEntries(translates.toArray[CharSequence])
        translate.setEntryValues(translatesValues.toArray[CharSequence])
        onPreferenceChangeListener(translate, (newValue) => {
          runUi(setTypeTranslated(newValue))
          true
        })
    }

    val languagesSortedTuples = LanguageType.toSortedTuples()

    val languages: List[String] = languagesSortedTuples map (_._2)
    val languagesValues: List[String] = languagesSortedTuples map (_._1)

    fromLanguage foreach {
      from =>
        from.setEntries(languages.toArray[CharSequence])
        from.setEntryValues(languagesValues.toArray[CharSequence])
        onPreferenceChangeListener(from, (newValue) => {
          runUi(changeFrom(newValue))
          true
        })
    }

    toLanguage foreach {
      to =>
        to.setEntries(languages.toArray[CharSequence])
        to.setEntryValues(languagesValues.toArray[CharSequence])
        onPreferenceChangeListener(to, (newValue) => {
          runUi(changeTo(newValue))
          true
        })
    }

    persistentServices.getLanguages(GetLanguagesRequest()).mapUi {
      response =>
        changeFrom(response.from.toString) ~
          changeTo(response.to.toString)
    }
  }


  private[this] def setTypeTranslated(key: String): Ui[_] = Ui {
    headUpNotification foreach (_.setEnabled(key.equals(Notification.toString)))
    typeTranslate foreach {
      translate =>
        key match {
          case k if k.equals(Notification.toString) =>
            translate.setTitle(R.string.notificationTitle)
            translate.setSummary(R.string.notificationMessage)
          case _ =>
            translate.setTitle(R.string.bubbleTitle)
            translate.setSummary(R.string.bubbleMessage)
        }
    }
  }

  private[this] def changeTo(key: String)(implicit contextWrapper: ContextWrapper): Ui[_] = Ui {
    for {
      toNameLang <- resGetString(key)
      pref <- toLanguage
    } yield pref.setTitle(getString(R.string.to, toNameLang))
  }

  private[this] def changeFrom(key: String)(implicit contextWrapper: ContextWrapper): Ui[_] = Ui {
    for {
      fromNameLang <- resGetString(key)
      pref <- fromLanguage
    } yield pref.setTitle(getString(R.string.from, fromNameLang))
  }

  private[this] def onPreferenceClickListener(pref: PreferenceScreen, f: ClickListenerFunction) = {
    pref.setOnPreferenceClickListener(new OnPreferenceClickListener {
      override def onPreferenceClick(preference: Preference): Boolean = f()
    })
  }

  private[this] def onPreferenceChangeListener(pref: ListPreference, f: ChangeListenerFunction) = {
    pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
      override def onPreferenceChange(preference: Preference, newValue: scala.Any): Boolean = f(newValue.toString)
    })
  }

  private[this] def showAboutDialog(implicit contextWrapper: ContextWrapper) = {
    Option(getActivity) foreach {
      activity =>
        val builder = new AlertDialog.Builder(activity)
        builder
          .setMessage(R.string.aboutMessage)
          .setPositiveButton(R.string.goTo47Deg,
            new DialogInterface.OnClickListener() {
              def onClick(dialog: DialogInterface, id: Int) {
                val webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resGetString(R.string.fortySevenUrl)))
                startActivity(webIntent)
                analyticsServices.send(analyticsGoTo47Deg)
                dialog.dismiss()
              }
            })
          .setNeutralButton(R.string.goToMyMemory,
            new DialogInterface.OnClickListener() {
              def onClick(dialog: DialogInterface, id: Int) {
                val webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resGetString(R.string.myMemoryUrl)))
                startActivity(webIntent)
                analyticsServices.send(analyticsGoToMyMemory)
                dialog.dismiss()
              }
            })
        val dialog = builder.create()
        dialog.show()
    }
  }

  private[this] def showOpenSourceDialog(implicit contextWrapper: ContextWrapper) = {
    Option(getActivity) foreach {
      activity =>
        val builder = new AlertDialog.Builder(activity)
        builder
          .setMessage(R.string.openSourceMessage)
          .setPositiveButton(R.string.goToGitHub,
            new DialogInterface.OnClickListener() {
              def onClick(dialog: DialogInterface, id: Int) {
                val webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resGetString(R.string.gitHubProjectUrl)))
                startActivity(webIntent)
                analyticsServices.send(analyticsGoToGitHub)
                dialog.dismiss()
              }
            })
          .setNeutralButton(R.string.goToWeb,
            new DialogInterface.OnClickListener() {
              def onClick(dialog: DialogInterface, id: Int) {
                val webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resGetString(R.string.translateBubbleUrl)))
                startActivity(webIntent)
                analyticsServices.send(analyticsGoToWebProject)
                dialog.dismiss()
              }
            })
        val dialog = builder.create()
        dialog.show()
    }
  }

}
