/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain
 *  a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.fortysevendeg.translatebubble.ui.commons

import android.widget.{Button, ImageView, LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.translatebubble.R
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext}

trait PlaceHolderLayout
  extends PlaceHolderStyles {

  var reloadButton = slot[Button]

  var image = slot[ImageView]

  var text = slot[TextView]

  def placeholder(implicit appContext: AppContext, context: ActivityContext) = {
    l[LinearLayout](
      w[ImageView] <~ placeholderImageStyle <~ wire(image),
      w[TextView] <~ placeholderMessageStyle <~ wire(text),
      w[Button] <~ placeholderButtonStyle <~ wire(reloadButton)
    ) <~ placeholderContentStyle
  }

  def loadFailed() = load(R.string.generalMessageError)

  def loadEmpty() = load(R.string.generalMessageEmpty, false)

  private def load(messageRes: Int, showButton: Boolean = true) = {
    runUi(
      (text <~ tvText(messageRes)) ~
        (reloadButton <~ (if (showButton) vVisible else vGone)))
  }

}
