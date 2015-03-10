package com.fortysevendeg.translatebubble.utils

import android.content.Context
import com.fortysevendeg.translatebubble.provider.{TranslateBubbleContentProvider, TranslateBubbleSqlHelper}
import macroid.AppContext

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
}
