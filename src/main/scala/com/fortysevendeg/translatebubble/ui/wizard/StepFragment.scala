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

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts}

class StepFragment
    extends Fragment
    with Contexts[Fragment]
    with ComponentRegistryImpl  {

  override lazy val contextProvider: ContextWrapper = fragmentContextWrapper

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val steps = Steps.steps
    val element = getArguments.getInt(StepFragment.keyStepPosition, 0)
    val step = steps(element)
    val fLayout = new FragmentLayout
    runUi(
      (fLayout.image <~ ivSrc(step.image))
          ~ (fLayout.title <~ tvText(step.title))
          ~ (fLayout.description <~ tvText(step.description))
    )
    fLayout.layout
  }

}

object StepFragment {
  val keyStepPosition = "step_position"
}
