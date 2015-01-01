package com.fortysevendeg.translatebubble.ui.wizard

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.ui.preferences.MainActivity
import macroid.{Ui, AppContext, Contexts}
import macroid.FullDsl._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._

class StepFragment
    extends Fragment
    with Contexts[Fragment]
    with ComponentRegistryImpl  {

  override implicit lazy val appContextProvider: AppContext = fragmentAppContext

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val steps = Steps.steps
    val element = getArguments.getInt(StepFragment.keyStepPosition, 0)
    val step = steps(element)
    val fLayout = new FragmentLayout
    runUi(
      (fLayout.image <~ ivSrc(step.image))
          ~ (fLayout.title <~ tvText(step.title))
          ~ (fLayout.description <~ tvText(step.description))
          ~ (fLayout.agree <~ (if (element >= steps.length -1) vVisible else vInvisible))
          ~ (fLayout.agree <~ On.click {
            Ui {
              persistentServices.wizardWasSeen()
              startActivity(new Intent(getActivity, classOf[MainActivity]))
              getActivity.finish()
            }
      })
    )
    fLayout.layout
  }

}

object StepFragment {
  val keyStepPosition = "step_position"
}
