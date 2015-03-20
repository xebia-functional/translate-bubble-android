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

package com.fortysevendeg.translatebubble.modules.repository.impl

import android.content.ContentValues
import android.database.Cursor
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.repository._
import com.fortysevendeg.translatebubble.provider.TranslationHistoryEntity._
import com.fortysevendeg.translatebubble.provider.{TranslateBubbleSqlHelper, TranslateBubbleContentProvider, TranslationHistoryEntity}
import com.fortysevendeg.translatebubble.service.Service
import com.fortysevendeg.translatebubble.utils.DBUtils
import com.fortysevendeg.translatebubble.utils.LanguageTypeTransformer._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait RepositoryServicesComponentImpl extends RepositoryServicesComponent with DBUtils {
  self: AppContextProvider =>

  lazy val repositoryServices = new RepositoryServicesImpl

  class RepositoryServicesImpl
      extends RepositoryServices {
    override def addTranslationHistory: Service[AddTranslationHistoryRequest, AddTranslationHistoryResponse] =
      request => {
        tryToFuture {
          Try {
            val contentValues = new ContentValues()
            contentValues.put(originalText, request.data.originalText)
            contentValues.put(translatedText, request.data.translatedText)
            contentValues.put(fromLanguage, toMyMemory(request.data.from))
            contentValues.put(toLanguage, toMyMemory(request.data.to))

            val uri = appContextProvider.get.getContentResolver.insert(
              TranslateBubbleContentProvider.contentUriTranslationHistory,
              contentValues)

            AddTranslationHistoryResponse(
              success = true,
              message = "The translation history item has been added successfully",
              translationHistoryEntity = Some(TranslationHistoryEntity(
                id = Integer.parseInt(uri.getPathSegments.get(1)),
                data = request.data)))

          } recover {
            case e: Exception =>
              AddTranslationHistoryResponse(
                success = false,
                message = "Unexpected error when adding a translation history item",
                translationHistoryEntity = None)
          }
        }
      }

    override def deleteTranslationHistory: Service[DeleteTranslationHistoryRequest, DeleteTranslationHistoryResponse] =
      request => {
        tryToFuture {
          Try {
            appContextProvider.get.getContentResolver.delete(
              TranslateBubbleContentProvider.contentUriTranslationHistory,
              s"${TranslateBubbleSqlHelper.id}=?",
              Seq(request.entity.id.toString).toArray)

            DeleteTranslationHistoryResponse(
              success = true,
              message = "The translation history item has been deleted successfully")

          } recover {
            case e: Exception =>
              DeleteTranslationHistoryResponse(
                success = false,
                message = "Unexpected error when deleting a translation history item")
          }
        }
      }

    override def fetchTranslationHistory:
    Service[FetchTranslationHistoryRequest, FetchTranslationHistoryResponse] =
      request =>
        tryToFuture {
          Try {
            val cursor: Option[Cursor] = Option(appContextProvider.get.getContentResolver.query(
              TranslateBubbleContentProvider.contentUriTranslationHistory,
              allFields.toArray,
              s"$fromLanguage=? AND $toLanguage=? AND $originalText=?",
              Seq(toMyMemory(request.from), toMyMemory(request.to), request.originalText).toArray,
              ""))

            FetchTranslationHistoryResponse(getEntityFromCursor(cursor, translationHistoryEntityFromCursor))
          }
        }

    override def fetchAllTranslationHistory:
    Service[FetchAllTranslationHistoryRequest, FetchAllTranslationHistoryResponse] =
      request =>
        tryToFuture {
          Try {
            val cursor: Option[Cursor] = Option(appContextProvider.get.getContentResolver.query(
              TranslateBubbleContentProvider.contentUriTranslationHistory,
              allFields.toArray,
              "",
              Seq.empty.toArray,
              ""))

            FetchAllTranslationHistoryResponse(getListFromCursor(cursor, translationHistoryEntityFromCursor))
          }
        }

    private def tryToFuture[A](function: => Try[A])(implicit ec: ExecutionContext): Future[A] =
      Future(function).flatMap {
        case Success(success) => Future.successful(success)
        case Failure(failure) => Future.failed(failure)
      }
  }

}
