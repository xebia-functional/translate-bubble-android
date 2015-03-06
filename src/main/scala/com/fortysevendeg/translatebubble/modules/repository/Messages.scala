package com.fortysevendeg.translatebubble.modules.repository

import com.fortysevendeg.translatebubble.provider.{TranslationHistoryEntity, TranslationHistoryEntityData}

case class AddTranslationHistoryRequest(data: TranslationHistoryEntityData)

case class AddTranslationHistoryResponse(success: Boolean, message: String, translationHistoryEntity: Option[TranslationHistoryEntity])

case class FetchAllTranslationHistoryRequest()

case class FetchAllTranslationHistoryResponse(result: Seq[TranslationHistoryEntity])