/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortysevendeg.translatebubble.ui.preferences

import android.app.{Activity, AlertDialog}
import android.content.{DialogInterface, Intent}
import android.net.Uri
import android.os.Bundle
import android.preference.Preference.{OnPreferenceChangeListener, OnPreferenceClickListener}
import android.preference._
import com.fortysevendeg.macroid.extras.PreferencesBuildingExtra._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.RootPreferencesFragment
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.commons.ContextWrapperProvider
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.persistent.GetLanguagesRequest
import com.fortysevendeg.translatebubble.ui.bubbleservice.BubbleService
import com.fortysevendeg.translatebubble.ui.commons.Strings._
import com.fortysevendeg.translatebubble.ui.history.TranslationHistoryActivity
import com.fortysevendeg.translatebubble.ui.wizard.WizardActivity
import com.fortysevendeg.translatebubble.utils.{TranslateUiType, Notification, LanguageType}
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts, Ui}

class MainActivity
  extends Activity
  with Contexts[Activity]
  with ContextWrapperProvider {

  override lazy val contextProvider: ContextWrapper = activityContextWrapper

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    BubbleService.launchIfIsNecessary(this)
    getFragmentManager.beginTransaction.replace(android.R.id.content, new DefaultPreferencesFragment()).commit
  }

}

class DefaultPreferencesFragment
  extends PreferenceFragment
  with Contexts[PreferenceFragment]
  with ComponentRegistryImpl {

  override lazy val contextProvider: ContextWrapper = fragmentContextWrapper

  implicit lazy val rootPreferencesFragment = new RootPreferencesFragment(this, R.xml.preferences)

  private lazy val typeTranslate = connect[ListPreference]("typeTranslate")
  private lazy val headUpNotification = connect[CheckBoxPreference]("headUpNotification")
  private lazy val toLanguage = connect[ListPreference]("toLanguage")
  private lazy val fromLanguage = connect[ListPreference]("fromLanguage")
  private lazy val openSource = connect[PreferenceScreen]("openSource")
  private lazy val showTutorial = connect[PreferenceScreen]("showTutorial")
  private lazy val showHistory = connect[PreferenceScreen]("showHistory")
  private lazy val about = connect[PreferenceScreen]("about")

  override def onCreate(savedInstanceState: Bundle) {

    super.onCreate(savedInstanceState)

    analyticsServices.send(analyticsPreferencesScreen)

    showTutorial foreach (onPreferenceClickListener(_,
      () => {
        val intent = new Intent(getActivity, classOf[WizardActivity])
        val bundle = new Bundle()
        bundle.putBoolean(WizardActivity.keyModeTutorial, true)
        intent.putExtras(bundle)
        getActivity.startActivity(intent)
        true
      }))

    showHistory foreach (onPreferenceClickListener(_,
      () => {
        val intent = new Intent(getActivity, classOf[TranslationHistoryActivity])
        getActivity.startActivity(intent)
        true
      }))

    openSource foreach (onPreferenceClickListener(_,
      () => {
        analyticsServices.send(analyticsOpenSourceDialog)
        showOpenSourceDialog()
        true
      }))

    about foreach (onPreferenceClickListener(_,
      () => {
        analyticsServices.send(analyticsOpenSourceDialog)
        showAboutDialog()
        true
      }))

    typeTranslate foreach {
      translate =>
        setTypeTranslated(translate.getValue)
        val translateSortedTuples = TranslateUiType.toSortedTuples()
        val translates: List[String] = translateSortedTuples map (_._2)
        val translatesValues: List[String] = translateSortedTuples map (_._1)
        translate.setEntries(translates.toArray[CharSequence])
        translate.setEntryValues(translatesValues.toArray[CharSequence])
        onPreferenceChangeListener(translate, (newValue) => {
          setTypeTranslated(newValue)
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
          changeFrom(newValue)
          true
        })
    }

    toLanguage foreach {
      to =>
        to.setEntries(languages.toArray[CharSequence])
        to.setEntryValues(languagesValues.toArray[CharSequence])
        onPreferenceChangeListener(to, (newValue) => {
          changeTo(newValue)
          true
        })
    }

    persistentServices.getLanguages(GetLanguagesRequest()).mapUi {
      response =>
        Ui {
          changeFrom(response.from.toString)
          changeTo(response.to.toString)
        }
    }


  }

  private def setTypeTranslated(key: String) = {
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

  private def changeTo(key: String) = {
    val toNameLang: String = getString(getResources.getIdentifier(key, "string", getActivity.getPackageName))
    toLanguage foreach (_.setTitle(getString(R.string.to, toNameLang)))
  }

  private def changeFrom(key: String) = {
    val fromNameLang: String = getString(getResources.getIdentifier(key, "string", getActivity.getPackageName))
    fromLanguage foreach (_.setTitle(getString(R.string.from, fromNameLang)))
  }

  type ClickListenerFunction = () => Boolean
  type ChangeListenerFunction = (String) => Boolean

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

  private[this] def showAboutDialog() = {
    val builder = new AlertDialog.Builder(getActivity)
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

  private[this] def showOpenSourceDialog() = {
    val builder = new AlertDialog.Builder(getActivity)
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
