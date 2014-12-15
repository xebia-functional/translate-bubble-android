package com.fortysevendeg.translatebubble.modules.translate

import com.fortysevendeg.translatebubble.utils.TypeLanguage.TypeLanguage

case class TranslateRequest(text: Option[String], from: TypeLanguage, to: TypeLanguage)

case class TranslateResponse(translated: Option[String])