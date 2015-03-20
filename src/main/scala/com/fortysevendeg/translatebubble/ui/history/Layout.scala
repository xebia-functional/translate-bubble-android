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

package com.fortysevendeg.translatebubble.ui.history

import android.support.v7.widget.{CardView, RecyclerView}
import android.widget.{ImageView, LinearLayout, TextView}
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext}

class TranslationHistoryLayoutAdapter(implicit context: ActivityContext, appContext: AppContext)
    extends Styles {

  var languages = slot[TextView]

  var originalText = slot[TextView]

  var translatedText = slot[TextView]

  val content = layout

  private def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[CardView](
      l[LinearLayout](
        l[LinearLayout](
          w[TextView] <~ wire(languages) <~ languagesStyle
        ) <~ translateTitleStyle,
        w[TextView] <~ wire(originalText) <~ originalTextStyle,
        w[TextView] <~ wire(translatedText) <~ translatedTextStyle
      ) <~ translateStyle
    ) <~ cardStyle
  )
}

class TranslationHistoryViewHolder(adapter: TranslationHistoryLayoutAdapter)(implicit context: ActivityContext, appContext: AppContext)
    extends RecyclerView.ViewHolder(adapter.content) {

  val content = adapter.content

  val languages = adapter.languages

  val originalText = adapter.originalText

  val translatedText = adapter.translatedText

}
