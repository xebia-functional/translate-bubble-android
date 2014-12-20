package com.fortysevendeg.translatebubble.macroid

import android.preference.{Preference, PreferenceFragment}

object PreferencesBuildingExtra {

  def connect[W <: Preference](preference: String)(implicit root: RootPreferencesFragment): Option[W] = {
    Some(root.fragment.findPreference(preference).asInstanceOf[W])
  }

}

case class RootPreferencesFragment(fragment: PreferenceFragment, preferenceResId: Int) {
  fragment.addPreferencesFromResource(preferenceResId)
}