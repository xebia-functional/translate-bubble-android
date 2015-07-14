package com.fortysevendeg.translatebubble.ui.wizard

import android.os.Bundle
import android.support.v4.app.{Fragment, FragmentPagerAdapter, FragmentManager}
import com.fortysevendeg.translatebubble.R
import macroid.ContextWrapper

class StepsPagerAdapter(fragmentManager: FragmentManager)(implicit contextWrapper: ContextWrapper)
  extends FragmentPagerAdapter(fragmentManager) {

  val steps = Steps.steps

  override def getItem(position: Int): Fragment = {
    val fragment = new StepFragment()
    val bundle = new Bundle()
    bundle.putInt(StepFragment.keyStepPosition, position)
    fragment.setArguments(bundle)
    fragment
  }

  override def getCount = {
    steps.length
  }
}

object Steps {

  def steps(implicit contextWrapper: ContextWrapper) = List(
    Step(
      R.drawable.wizard_icon,
      contextWrapper.application.getString(R.string.wizardTitle1),
      contextWrapper.application.getString(R.string.wizardDescription1)),
    Step(
      R.drawable.wizard_step_01,
      contextWrapper.application.getString(R.string.wizardTitle2),
      contextWrapper.application.getString(R.string.wizardDescription2)),
    Step(
      R.drawable.wizard_step_02,
      contextWrapper.application.getString(R.string.wizardTitle3),
      contextWrapper.application.getString(R.string.wizardDescription3)),
    Step(
      R.drawable.wizard_step_03,
      contextWrapper.application.getString(R.string.wizardTitle4),
      contextWrapper.application.getString(R.string.wizardDescription4)),
    Step(
      R.drawable.wizard_step_04,
      contextWrapper.application.getString(R.string.wizardTitle5),
      contextWrapper.application.getString(R.string.wizardDescription5)))

}

case class Step(image: Int, title: String, description: String)