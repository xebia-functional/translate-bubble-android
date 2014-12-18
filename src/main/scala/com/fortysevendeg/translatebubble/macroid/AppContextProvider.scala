package com.fortysevendeg.translatebubble.macroid

import macroid.AppContext

trait AppContextProvider {

  implicit val appContextProvider : AppContext

}
