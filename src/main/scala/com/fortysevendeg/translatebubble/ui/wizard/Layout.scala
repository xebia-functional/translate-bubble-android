package com.fortysevendeg.translatebubble.ui.wizard

import android.support.v4.view.ViewPager
import android.widget._
import macroid.FullDsl._
import macroid.{IdGeneration, ActivityContext, AppContext}
import Styles._

trait Layout extends IdGeneration {

  var pager = slot[ViewPager]

  def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[FrameLayout](
      l[ViewPager]() <~ wire(pager) <~ pagerStyle <~ id(Id.pager) // ViewPager need set resource id
    ) <~ rootStyle
  )

}

class FragmentLayout(implicit appContext: AppContext, context: ActivityContext) {

  var image = slot[ImageView]

  var title = slot[TextView]

  var description = slot[TextView]

  var agree = slot[Button]

  val content = getUi(
    l[LinearLayout](
      w[ImageView] <~ placeHolderStyle <~ wire(image),
      w[TextView] <~ titleStepStyle <~ wire(title),
      w[TextView] <~ descriptionStepStyle <~ wire(description),
      w[Button] <~ agreeStepStyle <~ wire(agree)
    ) <~ contentStepsStyle
  )

  def layout = content

}