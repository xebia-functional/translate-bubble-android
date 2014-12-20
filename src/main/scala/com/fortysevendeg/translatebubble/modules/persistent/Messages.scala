package com.fortysevendeg.translatebubble.modules.persistent

import com.fortysevendeg.translatebubble.utils.LanguageType.LanguageType

case class GetLanguagesRequest()

case class GetLanguagesResponse(from: LanguageType, to: LanguageType)
