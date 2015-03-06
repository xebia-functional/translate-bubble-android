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
        //      w[ImageView] <~ lineStyle,
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
