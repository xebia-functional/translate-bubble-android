package com.fortysevendeg.translatebubble.provider

import android.content.{ContentProvider, ContentUris, ContentValues, UriMatcher}
import android.database.Cursor
import android.database.sqlite.{SQLiteDatabase, SQLiteQueryBuilder}
import android.net.Uri
import TranslateBubbleContentProvider._

class TranslateBubbleContentProvider extends ContentProvider {
  
  lazy val translateBubbleSqlHelper = new TranslateBubbleSqlHelper(getContext)
  lazy val database: Option[SQLiteDatabase] = Option[SQLiteDatabase](translateBubbleSqlHelper.getWritableDatabase)

  override def onCreate(): Boolean = database match {
    case Some(databaseObject) if databaseObject.isOpen => true
    case _ => false
  }

  override def onLowMemory() {
    super.onLowMemory()
    translateBubbleSqlHelper.close()
  }

  override def getType(uri: Uri): String = uriMatcher.`match`(uri) match {
    case code if code == codeTranslationHistoryAllItems => mimeTypeAllItems
    case code if code == codeTranslationHistorySingleItem => mimeTypeSingleItem
    case _ => throw new IllegalArgumentException(invalidUri + uri)
  }

  override def update(uri: Uri, values: ContentValues, selection: String, selectionArgs: Array[String]): Int =
    uriMatcher.`match`(uri) match {
      case code if code == codeTranslationHistoryAllItems =>
        getOrOpenDatabase.update(TranslationHistoryEntity.table, values, selection, selectionArgs)
      case code if code == codeTranslationHistorySingleItem =>
        getOrOpenDatabase.update(
          TranslationHistoryEntity.table,
          values,
          s"${TranslateBubbleSqlHelper.id} = ?",
          Seq(uri.getPathSegments.get(1)).toArray)
      case _ => throw new IllegalArgumentException(invalidUri + uri)
    }

  override def insert(uri: Uri, values: ContentValues): Uri = uriMatcher.`match`(uri) match {
    case code if code == codeTranslationHistoryAllItems =>
      ContentUris.withAppendedId(
        contentUriTranslationHistory,
        getOrOpenDatabase.insert(TranslationHistoryEntity.table, TranslateBubbleSqlHelper.databaseName, values))
    case _ => throw new IllegalArgumentException(invalidUri + uri)
  }

  override def delete(uri: Uri, selection: String, selectionArgs: Array[String]): Int = uriMatcher.`match`(uri) match {
    case code if code == codeTranslationHistoryAllItems =>
      getOrOpenDatabase.delete(TranslationHistoryEntity.table, selection, selectionArgs)
    case code if code == codeTranslationHistorySingleItem =>
      getOrOpenDatabase.delete(
        TranslationHistoryEntity.table,
        s"${TranslateBubbleSqlHelper.id} = ?",
        Seq(uri.getPathSegments.get(1)).toArray)
    case _ => throw new IllegalArgumentException(invalidUri + uri)
  }

  override def query(
      uri: Uri,
      projection: Array[String],
      selection: String,
      selectionArgs: Array[String],
      sortOrder: String): Cursor = {
    uriMatcher.`match`(uri) match {
      case code if code == codeTranslationHistoryAllItems =>
        val queryBuilder = new SQLiteQueryBuilder()
        queryBuilder.setTables(TranslationHistoryEntity.table)
        queryBuilder.query(getOrOpenDatabase, projection, selection, selectionArgs, null, null, null)
      case code if code == codeTranslationHistorySingleItem =>
        val queryBuilder = new SQLiteQueryBuilder()
        queryBuilder.setTables(TranslationHistoryEntity.table)
        queryBuilder.query(
          getOrOpenDatabase,
          projection,
          s"${TranslateBubbleSqlHelper.id} = ?",
          Seq(uri.getPathSegments.get(1)).toArray,
          null,
          null,
          null)
      case _ => throw new IllegalArgumentException(invalidUri + uri)
    }
  }

  private def getOrOpenDatabase = database match {
    case Some(databaseObject) if databaseObject.isOpen => databaseObject
    case _ => translateBubbleSqlHelper.getWritableDatabase
  }
}

object TranslateBubbleContentProvider {
  val invalidUri = "Invalid uri: "
  val authorityPart = "com.fortysevendeg.translatebubble"
  val contentPrefix = "content://"
  val contentUriTranslationHistory = Uri.parse(contentPrefix + authorityPart + "/" + TranslationHistoryEntity.table)
  val codeTranslationHistoryAllItems = 1
  val codeTranslationHistorySingleItem = 2
  val mimeTypeAllItems = "vnd.android.cursor.dir/vnd.com.fortysevendeg.translatebubble"
  val mimeTypeSingleItem = "vnd.android.cursor.item/vnd.com.fortysevendeg.translatebubble"

  val uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  uriMatcher.addURI(authorityPart, TranslationHistoryEntity.table, codeTranslationHistoryAllItems);
  uriMatcher.addURI(authorityPart, s"${TranslationHistoryEntity.table}/#", codeTranslationHistorySingleItem);
} 