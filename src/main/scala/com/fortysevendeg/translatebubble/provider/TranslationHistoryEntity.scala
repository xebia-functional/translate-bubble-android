/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
