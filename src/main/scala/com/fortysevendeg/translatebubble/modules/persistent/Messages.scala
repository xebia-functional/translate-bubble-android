package com.fortysevendeg.translatebubble.modules.persistent

import com.fortysevendeg.translatebubble.utils.TypeLanguage.TypeLanguage

case class GetLanguagesRequest()

case class GetLanguagesResponse(from: TypeLanguage, to: TypeLanguage)
