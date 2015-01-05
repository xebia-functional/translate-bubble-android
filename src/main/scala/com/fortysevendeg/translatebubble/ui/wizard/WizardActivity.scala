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
    setContentView(layout)

    val steps = Steps.steps.length

    // TODO we should create a new Tweak for ViewPagers in MacroidExtras
    
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
    )
  )


}

case class Step(image: Int, title: String, description: String)
