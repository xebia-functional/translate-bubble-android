package com.fortysevendeg.translatebubble.ui.preferences

import android.os.Bundle
import android.preference._
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.ui.commons.Strings._
import macroid.{ContextWrapper, Contexts}

class DefaultPreferencesFragment
  extends PreferenceFragment
  with Contexts[PreferenceFragment]
  with ComponentRegistryImpl
  with Composer {

  override lazy val contextProvider: ContextWrapper = fragmentContextWrapper

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    analyticsServices.send(analyticsPreferencesScreen)
    initializePreferences
  }

}
