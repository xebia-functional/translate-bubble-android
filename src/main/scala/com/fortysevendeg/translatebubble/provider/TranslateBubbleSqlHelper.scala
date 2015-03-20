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

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.os.Handler
import com.fortysevendeg.translatebubble.utils.DBUtils

class TranslateBubbleSqlHelper(context: Context)
    extends SQLiteOpenHelper(context, TranslateBubbleSqlHelper.databaseName, null, TranslateBubbleSqlHelper.databaseVersion)
    with DBUtils {

  override def onCreate(db: SQLiteDatabase) = {
    db.execSQL("CREATE TABLE " + TranslationHistoryEntity.table +
        "(" + TranslateBubbleSqlHelper.id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        TranslationHistoryEntity.originalText + " TEXT not null, " +
        TranslationHistoryEntity.translatedText + " TEXT not null, " +
        TranslationHistoryEntity.fromLanguage + " TEXT not null, " +
        TranslationHistoryEntity.toLanguage + " TEXT not null)")

    new Handler().postDelayed(
      new Runnable() {
        override def run() = execAllVersionsDB(context)
      }, 0)
  }
  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {

    new Handler().post(
      new Runnable() {
        override def run() = execVersionsDB(context, oldVersion, newVersion)
      })
  }
}

object TranslateBubbleSqlHelper {
  val id = "_id"
  val databaseName = "translateBubble"
  val databaseVersion = 1
}
