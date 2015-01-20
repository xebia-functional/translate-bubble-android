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
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.ui.preferences.MainActivity
import macroid.FullDsl._
import macroid.{AppContext, Contexts, Transformer}
import com.fortysevendeg.translatebubble.ui.commons.Strings._

import scala.concurrent.ExecutionContext.Implicits.global

class WizardActivity
    extends FragmentActivity
    with Contexts[FragmentActivity]
    with Layout
    with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = activityAppContext

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    val extras = getIntent.getExtras
    val modeTutorial = (if (extras != null) extras.getBoolean(WizardActivity.keyModeTutorial, false) else false)
    if (!modeTutorial && persistentServices.isWizardWasSeen()) {
      startActivity(new Intent(this, classOf[MainActivity]))
      finish()
    }

    analyticsServices.send(if (modeTutorial) analyticsTutorialScreen else analyticsWizardScreen)

    setContentView(layout)

    val steps = Steps.steps.length

    for {
      page <- paginationContent
      pager <- viewPager
    } yield {
      def activateImages(position: Int) = Transformer {
        case i: ImageView if (i.getTag.equals("position_%d".format(position))) => i <~ vActivated(true)
        case i: ImageView => i <~ vActivated(false)
      }

      pager.setAdapter(new StepsPagerAdapter(getSupportFragmentManager()))
      pager.setOnPageChangeListener(new OnPageChangeListener {
        var isLastStep = false
        override def onPageScrollStateChanged(i: Int): Unit = {}
        override def onPageScrolled(i: Int, v: Float, i1: Int): Unit = {}
        override def onPageSelected(i: Int): Unit = {
          runUi(paginationContent <~ activateImages(i))
          if (!modeTutorial) {
            if (i >= steps - 1) {
              isLastStep = true
              runUi(
                (paginationContent <~~ (vGone ++ fadeOut(300))) ~
                    (gotIt <~ vVisible <~~ fadeIn(300))
              )
            } else if (isLastStep) {
              isLastStep = false
              runUi(
                (paginationContent <~ vVisible <~~ fadeIn(300)) ~
                    (gotIt <~~ (vGone ++ fadeOut(300)))
              )
            }
          }
        }
      })
      for (p <- 0 to (steps - 1)) {
        page.addView(pagination(p))
      }
      runUi(paginationContent <~ activateImages(0))
    }

  }

  class StepsPagerAdapter(fragmentManager: FragmentManager)
      extends FragmentPagerAdapter(fragmentManager) {

    val steps = Steps.steps

    override def getItem(position: Int): Fragment = {
      val fragment = new StepFragment()
      val bundle = new Bundle()
      bundle.putInt(StepFragment.keyStepPosition, position)
      fragment.setArguments(bundle)
      fragment
    }

    override def getCount() = {
      steps.length
    }
  }

}

object WizardActivity {
  val keyModeTutorial = "mode_tutorial"
}

object Steps {

  def steps(implicit appContext: AppContext) = List(
    new Step(
      R.drawable.wizard_icon,
      appContext.get.getString(R.string.wizardTitle1),
      appContext.get.getString(R.string.wizardDescription1)
    ),
    new Step(
      R.drawable.wizard_step_01,
      appContext.get.getString(R.string.wizardTitle2),
      appContext.get.getString(R.string.wizardDescription2)
    ),
    new Step(
      R.drawable.wizard_step_02,
      appContext.get.getString(R.string.wizardTitle3),
      appContext.get.getString(R.string.wizardDescription3)
    ),
    new Step(
      R.drawable.wizard_step_03,
      appContext.get.getString(R.string.wizardTitle4),
      appContext.get.getString(R.string.wizardDescription4)
    ),
    new Step(
      R.drawable.wizard_step_04,
      appContext.get.getString(R.string.wizardTitle5),
      appContext.get.getString(R.string.wizardDescription5)
    )
  )


}

case class Step(image: Int, title: String, description: String)
