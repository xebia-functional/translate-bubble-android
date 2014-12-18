package com.fortysevendeg.translatebubble.modules.translate

import com.fortysevendeg.translatebubble.utils.LanguageType.LanguageType

case class TranslateRequest(text: Option[String], from: LanguageType, to: LanguageType)

case class TranslateResponse(translated: Option[String])