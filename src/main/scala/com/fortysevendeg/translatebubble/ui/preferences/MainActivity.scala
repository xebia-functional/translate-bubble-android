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
import com.fortysevendeg.macroid.extras.{AppContextProvider, RootPreferencesFragment}
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.persistent.GetLanguagesRequest
import com.fortysevendeg.translatebubble.ui.bubbleservice.BubbleService
import com.fortysevendeg.translatebubble.ui.commons.Strings._
import com.fortysevendeg.translatebubble.ui.history.TranslationHistoryActivity
import com.fortysevendeg.translatebubble.ui.wizard.WizardActivity
import com.fortysevendeg.translatebubble.utils.{LanguageType, TranslateUIType}
import macroid.FullDsl._
import macroid.{AppContext, Contexts}

class MainActivity
    extends Activity
    with Contexts[Activity]
    with AppContextProvider {

  override implicit lazy val appContextProvider: AppContext = activityAppContext

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    BubbleService.launchIfIsNecessary
    getFragmentManager.beginTransaction.replace(android.R.id.content, new DefaultPreferencesFragment()).commit
  }

}

class DefaultPreferencesFragment
    extends PreferenceFragment
    with AppContextProvider
    with Contexts[PreferenceFragment]
    with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = fragmentAppContext

  implicit lazy val rootPreferencesFragment = new RootPreferencesFragment(this, R.xml.preferences)

  private lazy val launchFake = connect[PreferenceScreen]("launchFake")
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

    showTutorial map (
        _.setOnPreferenceClickListener(new OnPreferenceClickListener {
          override def onPreferenceClick(preference: Preference): Boolean = {
            val intent = new Intent(getActivity, classOf[WizardActivity])
            val bundle = new Bundle()
            bundle.putBoolean(WizardActivity.keyModeTutorial, true)
            intent.putExtras(bundle)
            getActivity.startActivity(intent)
            true
          }
        })
        )

    showHistory map (
        _.setOnPreferenceClickListener(new OnPreferenceClickListener {
          override def onPreferenceClick(preference: Preference): Boolean = {
            val intent = new Intent(getActivity, classOf[TranslationHistoryActivity])
            getActivity.startActivity(intent)
            true
          }
        })
        )

    openSource map (
        _.setOnPreferenceClickListener(new OnPreferenceClickListener {
          override def onPreferenceClick(preference: Preference): Boolean = {
            analyticsServices.send(analyticsOpenSourceDialog)
            val builder = new AlertDialog.Builder(getActivity)
            builder
                .setMessage(R.string.openSourceMessage)
                .setPositiveButton(R.string.goToGitHub,
                  new DialogInterface.OnClickListener() {
                    def onClick(dialog: DialogInterface, id: Int) {
                      val webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resGetString(R.string.gitHubProjectUrl)));
                      startActivity(webIntent);
                      analyticsServices.send(analyticsGoToGitHub)
                      dialog.dismiss()
                    }
                  })
                .setNeutralButton(R.string.goToWeb,
                  new DialogInterface.OnClickListener() {
                    def onClick(dialog: DialogInterface, id: Int) {
                      val webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resGetString(R.string.translateBubbleUrl)));
                      startActivity(webIntent);
                      analyticsServices.send(analyticsGoToWebProject)
                      dialog.dismiss()
                    }
                  })
            val dialog = builder.create()
            dialog.show()
            true
          }
        })
        )

    about map (
        _.setOnPreferenceClickListener(
          new OnPreferenceClickListener {
            override def onPreferenceClick(preference: Preference): Boolean = {
              analyticsServices.send(analyticsOpenSourceDialog)
              val builder = new AlertDialog.Builder(getActivity)
              builder
                  .setMessage(R.string.aboutMessage)
                  .setPositiveButton(R.string.goTo47Deg,
                    new DialogInterface.OnClickListener() {
                      def onClick(dialog: DialogInterface, id: Int) {
                        val webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resGetString(R.string.fortySevenUrl)));
                        startActivity(webIntent);
                        analyticsServices.send(analyticsGoTo47Deg)
                        dialog.dismiss()
                      }
                    })
                  .setNeutralButton(R.string.goToMyMemory,
                    new DialogInterface.OnClickListener() {
                      def onClick(dialog: DialogInterface, id: Int) {
                        val webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resGetString(R.string.myMemoryUrl)));
                        startActivity(webIntent);
                        analyticsServices.send(analyticsGoToMyMemory)
                        dialog.dismiss()
                      }
                    })
              val dialog = builder.create()
              dialog.show()
              true
            }
          })
        )

    typeTranslate map {
      translate =>
        setTypeTranslated(translate.getValue)
        val translateSortedTuples = TranslateUIType.toSortedTuples()
        val translates: List[String] = translateSortedTuples map (_._2)
        val translatesValues: List[String] = translateSortedTuples map (_._1)
        translate.setEntries(translates.toArray[CharSequence])
        translate.setEntryValues(translatesValues.toArray[CharSequence])
        translate.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
          def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
            setTypeTranslated(newValue.asInstanceOf[String])
            true
          }
        })
    }

    val languagesSortedTuples = LanguageType.toSortedTuples()

    val languages: List[String] = languagesSortedTuples map (_._2)
    val languagesValues: List[String] = languagesSortedTuples map (_._1)

    fromLanguage map {
      from =>
        from.setEntries(languages.toArray[CharSequence])
        from.setEntryValues(languagesValues.toArray[CharSequence])
        from.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
          def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
            changeFrom(newValue.asInstanceOf[String])
            true
          }
        })
    }

    toLanguage map {
      to =>
        to.setEntries(languages.toArray[CharSequence])
        to.setEntryValues(languagesValues.toArray[CharSequence])
        to.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
          def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
            changeTo(newValue.asInstanceOf[String])
            true
          }
        })
    }

    persistentServices.getLanguages(GetLanguagesRequest()).mapUi(
      response => {
        changeFrom(response.from.toString)
        changeTo(response.to.toString)
      }
    )

  }

  private def setTypeTranslated(key: String) = {
    headUpNotification.map(_.setEnabled(key.equals(TranslateUIType.NOTIFICATION.toString)))
    typeTranslate map {
      translate =>
        key match {
          case _ if key.equals(TranslateUIType.NOTIFICATION.toString) =>
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
    toLanguage map (_.setTitle(getString(R.string.to, toNameLang)))
  }

  private def changeFrom(key: String) = {
    val fromNameLang: String = getString(getResources.getIdentifier(key, "string", getActivity.getPackageName))
    fromLanguage map (_.setTitle(getString(R.string.from, fromNameLang)))
  }

}
