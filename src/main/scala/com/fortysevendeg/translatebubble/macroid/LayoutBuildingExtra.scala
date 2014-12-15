package com.fortysevendeg.translatebubble.macroid

import android.view.{LayoutInflater, ViewGroup}
import macroid.AppContext

object LayoutBuildingExtra {

  def connect[W](id: Int)(implicit root: RootView): Option[W] = {
    Some(root.view.findViewById(id).asInstanceOf[W])
  }

}

class RootView(layout: Int)(implicit appContext: AppContext) {
  val view = LayoutInflater.from(appContext.get).inflate(layout, null).asInstanceOf[ViewGroup]
}
