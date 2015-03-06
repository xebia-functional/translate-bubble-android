package com.fortysevendeg.translatebubble.provider

import android.database.Cursor
import com.fortysevendeg.translatebubble.utils.LanguageType.LanguageType

case class TranslationHistoryEntity(id: Int, data: TranslationHistoryEntityData) {

  //  def get(contentResolver: ContentResolver, id: Int): Option[TranslationHistoryEntityData] = {
  //    val cursor = contentResolver.query(
  //      TranslateBubbleContentProvider.contentUriTranslationHistory,
  //      TranslationHistoryEntity.allFields.toArray,
  //      TranslateBubbleSqlHelper.id + " = ?",
  //      Seq(id.toString).toArray,
  //      "")
  //
  //    get(Option[Cursor](cursor))
  //  }
  //
  //  def list(contentResolver: ContentResolver, where: String, sort: String): List[TranslationHistoryEntityData] = {
  //    val cursor = contentResolver.query(
  //      TranslateBubbleContentProvider.contentUriTranslationHistory,
  //      TranslationHistoryEntity.allFields.toArray,
  //      where,
  //      null,
  //      sort)
  //
  //    list(Option[Cursor](cursor))
  //  }
  //
  //  private def get(cursor: Option[Cursor]): Option[TranslationHistoryEntityData] = {
  //    cursor match {
  //      case Some(cursorItem) if cursorItem.moveToFirst() => {
  //        val single = translationHistoryEntityDataFromCursor(cursorItem)
  //        cursorItem.close()
  //        Some(single)
  //      }
  //      case _ => None
  //    }
  //  }
  //
  //  private def list(cursor: Option[Cursor]): List[TranslationHistoryEntityData] = {
  //
  //    def listLoop(cursor: Cursor, result: List[TranslationHistoryEntityData]): List[TranslationHistoryEntityData] = {
  //      cursor match {
  //        case validCursor if validCursor.moveToNext() => listLoop(
  //          cursor,
  //          translationHistoryEntityDataFromCursor(validCursor) :: result)
  //        case _ => result.reverse
  //      }
  //    }
  //
  //    cursor match {
  //      case Some(cursorItem) if cursorItem.moveToFirst() => {
  //        val result = listLoop(cursorItem, List.empty)
  //        cursorItem.close()
  //        result
  //      }
  //      case _ => List.empty
  //    }
  //  }

}

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
