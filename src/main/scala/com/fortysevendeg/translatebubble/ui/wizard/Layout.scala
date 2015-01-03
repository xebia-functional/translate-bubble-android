package com.fortysevendeg.translatebubble.ui.wizard

import android.support.v4.view.ViewPager
import android.widget._
import com.fortysevendeg.translatebubble.modules.persistent.PersistentServicesComponent
import com.fortysevendeg.translatebubble.ui.preferences.MainActivity
import macroid.FullDsl._
import macroid.{IdGeneration, ActivityContext, AppContext}
import Styles._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ExtraUIActions._

trait Layout extends IdGeneration {

  self: PersistentServicesComponent =>

  var viewPager = slot[ViewPager]

  var paginationContent = slot[LinearLayout]

  var gotIt = slot[Button]

  def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[LinearLayout](
      l[ViewPager]() <~ wire(viewPager) <~ pagerStyle <~ id(Id.pager), // ViewPager need set resource id
      l[FrameLayout](
        l[LinearLayout]() <~ wire(paginationContent) <~ paginationContentStyle,
        w[Button] <~ wire(gotIt) <~ gotItStyle <~ On.click {
          persistentServices.wizardWasSeen()
          context.get.finish()
          uiStartActivity[MainActivity]
        }
      ) <~ bottomContentStyle
    ) <~ rootStyle
  )

  def pagination(position: Int)(implicit appContext: AppContext, context: ActivityContext) = getUi(
    w[ImageView] <~ paginationItemStyle <~ vTag("position_%d".format(position))
  )

}

class FragmentLayout(implicit appContext: AppContext, context: ActivityContext) {

  var image = slot[ImageView]

  var title = slot[TextView]

  var description = slot[TextView]

  val content = getUi(
    l[LinearLayout](
      w[ImageView] <~ placeHolderStyle <~ wire(image),
      w[TextView] <~ titleStepStyle <~ wire(title),
      w[TextView] <~ descriptionStepStyle <~ wire(description)
    ) <~ contentStepsStyle
  )

  def layout = content

}