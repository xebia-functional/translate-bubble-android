package com.fortysevendeg.translatebubble.ui.wizard

import android.animation.{Animator, AnimatorListenerAdapter}
import android.content.Intent
import android.os.Bundle
import android.support.v4.app._
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.View
import android.view.View._
import android.view.animation.DecelerateInterpolator
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.ui.preferences.MainActivity
import macroid.{Snail, AppContext, Contexts}
import macroid.FullDsl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.Success

class WizardActivity
    extends FragmentActivity
    with Contexts[FragmentActivity]
    with Layout
    with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = activityAppContext

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    val extras = getIntent.getExtras
    val showTutorial = (if (extras != null) extras.getBoolean(WizardActivity.keyShowTutorial, false) else false)
    if (!showTutorial && persistentServices.isWizardWasSeen()) {
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
      pager.setAdapter(new StepsPagerAdapter(getSupportFragmentManager()))
      pager.setOnPageChangeListener(new OnPageChangeListener {
        var isLastStep = false
        override def onPageScrollStateChanged(i: Int): Unit = {}
        override def onPageScrolled(i: Int, v: Float, i1: Int): Unit = {}
        override def onPageSelected(i: Int): Unit = {
          for (p <- 0 to (steps - 1)) {
            page.getChildAt(p).setActivated(false)
          }
          page.getChildAt(i).setActivated(true)
          if (i >= steps - 1) {
            isLastStep = true
            runUi(
              (paginationContent <~~ WizardSnails.wizardFadeOut) ~
                  (gotIt <~~ WizardSnails.wizardFadeIn)
            )
          } else if (isLastStep) {
            isLastStep = false
            runUi(
              (paginationContent <~~ WizardSnails.wizardFadeIn) ~
                  (gotIt <~~ WizardSnails.wizardFadeOut)
            )
          }
        }
      })
      for (p <- 0 to (steps - 1)) {
        page.addView(pagination(p))
      }
      page.getChildAt(0).setActivated(true)
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
  val keyShowTutorial = "show_tutorial"
}

object Steps {

  def steps(implicit appContext: AppContext) = List(
    new Step(
      R.drawable.ic_launcher,
      appContext.get.getString(R.string.wizardTitle1),
      appContext.get.getString(R.string.wizardDescription1)
    ),
    new Step(
      R.drawable.ic_launcher,
      appContext.get.getString(R.string.wizardTitle2),
      appContext.get.getString(R.string.wizardDescription2)
    ),
    new Step(
      R.drawable.ic_launcher,
      appContext.get.getString(R.string.wizardTitle3),
      appContext.get.getString(R.string.wizardDescription3)
    ),
    new Step(
      R.drawable.ic_launcher,
      appContext.get.getString(R.string.wizardTitle4),
      appContext.get.getString(R.string.wizardDescription4)
    )
  )


}

case class Step(image: Int, title: String, description: String)

object WizardSnails {

  val DURATION = 300

  val wizardFadeIn = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.setVisibility(VISIBLE)
      view.setAlpha(0)
      view.animate
          .setDuration(DURATION)
          .alpha(1)
          .setInterpolator(new DecelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }

  val wizardFadeOut = Snail[View] {
    view ⇒
      val animPromise = Promise[Unit]()
      view.animate
          .setDuration(DURATION)
          .alpha(0)
          .setInterpolator(new DecelerateInterpolator())
          .setListener(new AnimatorListenerAdapter {
        override def onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          view.setVisibility(GONE)
          animPromise.complete(Success(()))
        }
      }).start()
      animPromise.future
  }
}