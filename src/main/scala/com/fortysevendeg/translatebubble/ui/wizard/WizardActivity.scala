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

package com.fortysevendeg.translatebubble.ui.wizard

import android.content.Intent
import android.os.Bundle
import android.support.v4.app._
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.ui.commons.Strings._
import com.fortysevendeg.translatebubble.ui.preferences.MainActivity
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts}

class WizardActivity
  extends FragmentActivity
  with Contexts[FragmentActivity]
  with Layout
  with ComponentRegistryImpl {

  override lazy val contextProvider: ContextWrapper = activityContextWrapper

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    val extras = getIntent.getExtras
    val modeTutorial = if (extras != null) extras.getBoolean(WizardActivity.keyModeTutorial, false) else false
    if (!modeTutorial && persistentServices.isWizardWasSeen()) {
      startActivity(new Intent(this, classOf[MainActivity]))
      finish()
    }
    analyticsServices.send(if (modeTutorial) analyticsTutorialScreen else analyticsWizardScreen)
    setContentView(layout)
    runUi(initializeUi(modeTutorial))
  }

}

object WizardActivity {
  val keyModeTutorial = "mode_tutorial"
}


