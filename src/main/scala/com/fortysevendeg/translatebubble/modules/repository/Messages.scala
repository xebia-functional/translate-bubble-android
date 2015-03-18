package com.fortysevendeg.translatebubble.modules.repository

import com.fortysevendeg.translatebubble.provider.{TranslationHistoryEntity, TranslationHistoryEntityData}
import com.fortysevendeg.translatebubble.utils.LanguageType.LanguageType

case class AddTranslationHistoryRequest(data: TranslationHistoryEntityData)

case class AddTranslationHistoryResponse(success: Boolean, message: String, translationHistoryEntity: Option[TranslationHistoryEntity])

case class DeleteTranslationHistoryRequest(entity: TranslationHistoryEntity)

case class DeleteTranslationHistoryResponse(success: Boolean, message: String)

case class FetchTranslationHistoryRequest(from: LanguageType, to: LanguageType, originalText: String)

case class FetchTranslationHistoryResponse(result: Option[TranslationHistoryEntity])

case class FetchAllTranslationHistoryRequest()

case class FetchAllTranslationHistoryResponse(result: Seq[TranslationHistoryEntity])