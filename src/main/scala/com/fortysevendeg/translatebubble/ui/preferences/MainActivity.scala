package com.fortysevendeg.translatebubble.ui.preferences

import android.app.Activity
import android.os.Bundle
import android.preference.Preference.{OnPreferenceChangeListener, OnPreferenceClickListener}
import android.preference._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.macroid.PreferencesBuildingExtra._
import com.fortysevendeg.translatebubble.macroid.{AppContextProvider, RootPreferencesFragment}
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.clipboard.CopyToClipboardRequest
import com.fortysevendeg.translatebubble.modules.persistent.GetLanguagesRequest
import com.fortysevendeg.translatebubble.ui.bubbleservice.BubbleService
import com.fortysevendeg.translatebubble.utils.LanguageType
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
  private lazy val typeBubble = connect[CheckBoxPreference]("typeBubble")
  private lazy val typeNotification = connect[CheckBoxPreference]("typeNotification")
  private lazy val headUpNotification = connect[CheckBoxPreference]("headUpNotification")
  private lazy val toLanguage = connect[ListPreference]("toLanguage")
  private lazy val fromLanguage = connect[ListPreference]("fromLanguage")

  override def onCreate(savedInstanceState: Bundle) {

    super.onCreate(savedInstanceState)

    launchFake.map(
      _.setOnPreferenceClickListener(new OnPreferenceClickListener {
        override def onPreferenceClick(preference: Preference): Boolean = {
          clipboardServices.copyToClipboard(CopyToClipboardRequest("Seattle is a coastal seaport city and the seat of King County, in the U.S. state of Washington. With an estimated 652,405 residents as of 2013, Seattle is the largest city in both the State of Washington and the Pacific Northwest region of North America and the fastest-growing major city in the United States.[5] The Seattle metropolitan area of around 3.6 million inhabitants is the 15th largest metropolitan area in the United States %d".format(System.currentTimeMillis())))
          true
        }
      })
    )

    // TODO Don't use 'map'. We should create a Tweak when MacroidExtra module works

    typeBubble.map(
      _.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
          val value: Boolean = newValue.asInstanceOf[Boolean]
          if (value) {
            typeNotification.map(_.setChecked(false))
            headUpNotification.map(_.setEnabled(false))
          }
          value
        }
      })
    )

    typeNotification.map(
      _.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
          val value: Boolean = newValue.asInstanceOf[Boolean]
          if (value) {
            typeBubble.map(_.setChecked(false))
            headUpNotification.map(_.setEnabled(true))
          }
          value
        }
      })
    )

    for {
      headUp <- headUpNotification
      notification <- typeNotification
    } yield headUp.setEnabled(notification.isChecked)

    val languages: List[String] = LanguageType.resourceNames
    val languagesValues: List[String] = LanguageType.stringNames()

    fromLanguage.map {
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

    toLanguage.map {
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

  private def changeTo(key: String) {
    val toNameLang: String = getString(getResources.getIdentifier(key, "string", getActivity.getPackageName))
    toLanguage.map(_.setTitle(getString(R.string.to, toNameLang)))
  }

  private def changeFrom(key: String) {
    val fromNameLang: String = getString(getResources.getIdentifier(key, "string", getActivity.getPackageName))
    fromLanguage.map(_.setTitle(getString(R.string.from, fromNameLang)))
  }

}
