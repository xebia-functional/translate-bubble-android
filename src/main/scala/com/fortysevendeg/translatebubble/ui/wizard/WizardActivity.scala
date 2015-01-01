package com.fortysevendeg.translatebubble.ui.wizard

import android.content.Intent
import android.os.Bundle
import android.support.v4.app._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.ui.preferences.MainActivity
import macroid.{AppContext, Contexts}

class WizardActivity
    extends FragmentActivity
    with Contexts[FragmentActivity]
    with Layout
    with ComponentRegistryImpl {

  override implicit lazy val appContextProvider: AppContext = activityAppContext

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    val extras = getIntent.getExtras
    val showTutorial = (if (extras != null)
      extras.getBoolean(WizardActivity.keyShowTutorial, false)
    else false)
    if (!showTutorial && persistentServices.isWizardWasSeen()) {
      startActivity(new Intent(this, classOf[MainActivity]))
      finish()
    }
    setContentView(layout)
    pager.map(_.setAdapter(new StepsPagerAdapter(getSupportFragmentManager())))
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
