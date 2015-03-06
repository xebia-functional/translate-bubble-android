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
        TranslationHistoryEntity.fromLanguage + " INTEGER not null, " +
        TranslationHistoryEntity.toLanguage + " INTEGER not null)")

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
