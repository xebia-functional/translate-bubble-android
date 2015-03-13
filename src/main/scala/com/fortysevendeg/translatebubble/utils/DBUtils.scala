package com.fortysevendeg.translatebubble.utils

import android.content.Context
import android.database.Cursor
import com.fortysevendeg.translatebubble.provider.{TranslateBubbleContentProvider, TranslateBubbleSqlHelper}
import macroid.AppContext

import scala.annotation.tailrec

trait DBUtils {

  def emptyAllTables(implicit appContext: AppContext) =
    appContext.get.getContentResolver.delete(
      TranslateBubbleContentProvider.contentUriTranslationHistory,
      "",
      Seq.empty.toArray)

  def execAllVersionsDB(context: Context) =
    for (i <- 1 to TranslateBubbleSqlHelper.databaseVersion) execVersion(context, i)

  def execVersionsDB(context: Context, oldVersion: Int, newVersion: Int) {
    for (i <- oldVersion + 1 to newVersion) execVersion(context, i)
  }

  def execVersion(context: Context, version: Int) {}

  def getEntityFromCursor[T](cursor: Option[Cursor], conversionFunction: Cursor => T): Option[T] = {
    cursor match {
      case Some(cursorObject) if cursorObject.moveToFirst() => {
        val result = Some(conversionFunction(cursorObject))
        cursorObject.close()
        result
      }
      case _ => None
    }
  }

  def getListFromCursor[T](cursor: Option[Cursor], conversionFunction: Cursor => T): Seq[T] = {
    @tailrec
    def getListFromEntityLoop(cursor: Cursor, result: Seq[T]): Seq[T] = {
      cursor match {
        case validCursor if validCursor.isAfterLast => result
        case _ => {
          val translationHistoryEntity = conversionFunction(cursor)
          cursor.moveToNext
          getListFromEntityLoop(cursor, translationHistoryEntity +: result)
        }
      }
    }

    cursor match {
      case Some(cursorObject) if cursorObject.moveToFirst() => {
        val result = getListFromEntityLoop(cursorObject, Seq.empty[T])
        cursorObject.close()
        result
      }
      case _ => Seq.empty[T]
    }
  }
}
