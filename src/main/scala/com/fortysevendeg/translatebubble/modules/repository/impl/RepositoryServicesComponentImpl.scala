package com.fortysevendeg.translatebubble.modules.repository.impl

import android.content.ContentValues
import android.database.Cursor
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.repository._
import com.fortysevendeg.translatebubble.provider.TranslationHistoryEntity._
import com.fortysevendeg.translatebubble.provider.{TranslateBubbleContentProvider, TranslationHistoryEntity}
import com.fortysevendeg.translatebubble.service.Service
import scala.concurrent.ExecutionContext.Implicits.global

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait RepositoryServicesComponentImpl extends RepositoryServicesComponent {
  self: AppContextProvider =>

  lazy val repositoryServices = new RepositoryServicesImpl

  class RepositoryServicesImpl
      extends RepositoryServices {
    override def addTranslationHistory: Service[AddTranslationHistoryRequest, AddTranslationHistoryResponse] =
      request => {

        import com.fortysevendeg.translatebubble.provider.TranslationHistoryEntity._
        import com.fortysevendeg.translatebubble.utils.LanguageTypeTransformer._

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

    override def fetchAllTranslationHistory:
    Service[FetchAllTranslationHistoryRequest, FetchAllTranslationHistoryResponse] =
      request =>
        tryToFuture {
          Try {
            val cursor: Option[Cursor] = Option(appContextProvider.get.getContentResolver.query(
              TranslateBubbleContentProvider.contentUriTranslationHistory,
              TranslationHistoryEntity.allFields.toArray,
              "",
              Seq.empty.toArray,
              ""))

            FetchAllTranslationHistoryResponse(getListFromEntity(cursor))
          }
        }

    private def tryToFuture[A](function: => Try[A])(implicit ec: ExecutionContext): Future[A] =
      Future(function).flatMap {
        case Success(success) => Future.successful(success)
        case Failure(failure) => Future.failed(failure)
      }

    private def getListFromEntity(cursor: Option[Cursor]) = {
      @tailrec
      def getListFromEntityLoop(cursor: Cursor, result: Seq[TranslationHistoryEntity]): Seq[TranslationHistoryEntity] = {
        cursor match {
          case validCursor if validCursor.isAfterLast => result
          case _ => {
            val translationHistoryEntity = translationHistoryEntityFromCursor(cursor)
            cursor.moveToNext
            getListFromEntityLoop(cursor, translationHistoryEntity +: result)
          }
        }
      }

      cursor match {
        case Some(cursorObject) if cursorObject.moveToFirst() => {
          val result = getListFromEntityLoop(cursorObject, Seq.empty[TranslationHistoryEntity])
          cursorObject.close()
          result
        }
        case _ => Seq.empty[TranslationHistoryEntity]
      }
    }

  }

}
