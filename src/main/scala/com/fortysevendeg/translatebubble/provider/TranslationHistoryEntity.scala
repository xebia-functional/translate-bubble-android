package com.fortysevendeg.translatebubble.provider

import android.database.Cursor
import com.fortysevendeg.translatebubble.utils.LanguageType.LanguageType

case class TranslationHistoryEntity(id: Int, data: TranslationHistoryEntityData)

case class TranslationHistoryEntityData(
    originalText: String,
    translatedText: String,
    from: LanguageType,
    to: LanguageType)

object TranslationHistoryEntity {
  val table = "translationHistory"
  val originalText = "originalText"
  val translatedText = "translatedText"
  val fromLanguage = "fromLanguage"
  val toLanguage = "toLanguage"

  val allFields = Seq[String](
    TranslateBubbleSqlHelper.id,
    originalText,
    translatedText,
    fromLanguage,
    toLanguage)

  import com.fortysevendeg.translatebubble.utils.LanguageTypeTransformer._

  def translationHistoryEntityFromCursor(cursor: Cursor) = {
    TranslationHistoryEntity(
      id = cursor.getInt(cursor.getColumnIndex(TranslateBubbleSqlHelper.id)),
      data = TranslationHistoryEntityData(
        originalText = cursor.getString(cursor.getColumnIndex(TranslationHistoryEntity.originalText)),
        translatedText = cursor.getString(cursor.getColumnIndex(TranslationHistoryEntity.translatedText)),
        from = fromMyMemory(cursor.getString(cursor.getColumnIndex(TranslationHistoryEntity.fromLanguage))),
        to = fromMyMemory(cursor.getString(cursor.getColumnIndex(TranslationHistoryEntity.toLanguage)))))
  }
}

object TranslationHistoryEntityData {

  import com.fortysevendeg.translatebubble.utils.LanguageTypeTransformer._

  def translationHistoryEntityDataFromCursor(cursor: Cursor) = {
    TranslationHistoryEntityData(
      originalText = cursor.getString(cursor.getColumnIndex(TranslationHistoryEntity.originalText)),
      translatedText = cursor.getString(cursor.getColumnIndex(TranslationHistoryEntity.translatedText)),
      from = fromMyMemory(cursor.getString(cursor.getColumnIndex(TranslationHistoryEntity.fromLanguage))),
      to = fromMyMemory(cursor.getString(cursor.getColumnIndex(TranslationHistoryEntity.toLanguage))))
  }
}
