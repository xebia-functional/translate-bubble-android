package com.fortysevendeg.translatebubble.ui.preferences

import android.app.Activity
import android.os.Bundle
import android.preference.Preference.{OnPreferenceChangeListener, OnPreferenceClickListener}
import android.preference._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.macroid.AppContextProvider
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.clipboard.CopyToClipboardRequest
import com.fortysevendeg.translatebubble.modules.persistent.GetLanguagesRequest
import com.fortysevendeg.translatebubble.ui.bubbleservice.BubbleService
import com.fortysevendeg.translatebubble.utils.TypeLanguage
import macroid.{AppContext, Contexts}
import macroid.FullDsl._

class MainActivity
    extends Activity
    with AppContextProvider
    with Contexts[Activity]
    with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = activityAppContext

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    BubbleService.launchIfIsNecessary
    getFragmentManager.beginTransaction.replace(android.R.id.content, new DefaultPreferencesFragment).commit
  }

  class DefaultPreferencesFragment
      extends PreferenceFragment
      with Contexts[PreferenceFragment] {

    private var launchFake: PreferenceScreen = null
    private var typeBubble: CheckBoxPreference = null
    private var typeNotification: CheckBoxPreference = null
    private var headUpNotification: CheckBoxPreference = null
    private var toLanguage: ListPreference = null
    private var fromLanguage: ListPreference = null

    override def onCreate(savedInstanceState: Bundle) {

      super.onCreate(savedInstanceState)
      addPreferencesFromResource(R.xml.preferences)

      launchFake = findPreference("launchFake").asInstanceOf[PreferenceScreen]
      launchFake.setOnPreferenceClickListener(new OnPreferenceClickListener {
        override def onPreferenceClick(preference: Preference): Boolean = {
          clipboardServices.copyToClipboard(CopyToClipboardRequest("Example Text %d".format(System.currentTimeMillis())))
          true
        }
      })

      typeBubble = findPreference("typeBubble").asInstanceOf[CheckBoxPreference]
      typeBubble.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
          val value: Boolean = newValue.asInstanceOf[Boolean]
          if (value) {
            typeNotification.setChecked(false)
            headUpNotification.setEnabled(false)
          }
          value
        }
      })

      typeNotification = findPreference("typeNotification").asInstanceOf[CheckBoxPreference]
      typeNotification.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
          val value: Boolean = newValue.asInstanceOf[Boolean]
          if (value) {
            typeBubble.setChecked(false)
            headUpNotification.setEnabled(true)
          }
          value
        }
      })

      headUpNotification = findPreference("headUpNotification").asInstanceOf[CheckBoxPreference]
      headUpNotification.setEnabled(typeNotification.isChecked)

      val languages: List[String] = TypeLanguage.resourceNames
      val languagesValues: List[String] = TypeLanguage.stringNames()
      fromLanguage = findPreference("fromLanguage").asInstanceOf[ListPreference]
      fromLanguage.setEntries(languages.toArray[CharSequence])
      fromLanguage.setEntryValues(languagesValues.toArray[CharSequence])
      fromLanguage.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
          changeFrom(newValue.asInstanceOf[String])
          true
        }
      })
      toLanguage = findPreference("toLanguage").asInstanceOf[ListPreference]
      toLanguage.setEntries(languages.toArray[CharSequence])
      toLanguage.setEntryValues(languagesValues.toArray[CharSequence])
      toLanguage.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        def onPreferenceChange(preference: Preference, newValue: AnyRef): Boolean = {
          changeTo(newValue.asInstanceOf[String])
          true
        }
      })

      persistentServices.getLanguages(GetLanguagesRequest()).mapUi(
        response => {
          changeFrom(response.from.toString)
          changeTo(response.to.toString)
        }
      )

    }

    private def changeTo(key: String) {
      val toNameLang: String = getString(getResources.getIdentifier(key, "string", getActivity.getPackageName))
      toLanguage.setTitle(getString(R.string.to, toNameLang))
    }

    private def changeFrom(key: String) {
      val fromNameLang: String = getString(getResources.getIdentifier(key, "string", getActivity.getPackageName))
      fromLanguage.setTitle(getString(R.string.from, fromNameLang))
    }

  }

}


