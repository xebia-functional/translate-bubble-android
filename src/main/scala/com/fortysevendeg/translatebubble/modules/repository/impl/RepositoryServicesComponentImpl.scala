package com.fortysevendeg.translatebubble.modules.repository.impl

import android.content.ContentValues
import android.database.Cursor
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.repository._
import com.fortysevendeg.translatebubble.provider.TranslationHistoryEntity._
import com.fortysevendeg.translatebubble.provider.{TranslateBubbleContentProvider, TranslationHistoryEntity}
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
