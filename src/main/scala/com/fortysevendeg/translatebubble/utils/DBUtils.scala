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
