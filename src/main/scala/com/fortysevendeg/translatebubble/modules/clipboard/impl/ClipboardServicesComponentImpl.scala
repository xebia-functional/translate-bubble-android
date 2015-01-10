package com.fortysevendeg.translatebubble.modules.clipboard.impl

import android.content.{ClipData, Context, ClipboardManager}
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.clipboard._
import com.fortysevendeg.translatebubble.service._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait ClipboardServicesComponentImpl
    extends ClipboardServicesComponent {

  self : AppContextProvider =>

  lazy val clipboardServices = new ClipboardServicesImpl

  class ClipboardServicesImpl extends ClipboardServices {

    var previousText: Option[String] = None

    var clipChangedListener: Option[ClipboardManager.OnPrimaryClipChangedListener] = None

    val clipboardManager: ClipboardManager = {
      appContextProvider.get.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
    }

    override def getText: Service[GetTextClipboardRequest, GetTextClipboardResponse] = request =>
      Future {
        Option(clipboardManager.getPrimaryClip) map (_.getItemAt(0)) map (_.getText) match {
          case Some(clipDataText) if (clipDataText.length > 0 && previousText.map(_ != clipDataText).getOrElse(true)) =>
            previousText = Some(clipDataText.toString)
            GetTextClipboardResponse(previousText)
          case _ => GetTextClipboardResponse(None)
        }
      }

    override def copyToClipboard: Service[CopyToClipboardRequest, CopyToClipboardResponse] = request =>
      Future {
        val clip = ClipData.newPlainText("label", request.text)
        clipboardManager.setPrimaryClip(clip)
        CopyToClipboardResponse()
      }

    def init(listener: ClipboardManager.OnPrimaryClipChangedListener): Unit = {
      if (clipChangedListener.isDefined) {
        clipChangedListener map clipboardManager.removePrimaryClipChangedListener
      }
      clipChangedListener = Some(listener)
      clipboardManager.addPrimaryClipChangedListener(listener)
    }

    def destroy(): Unit = {
      clipChangedListener map clipboardManager.removePrimaryClipChangedListener
      clipChangedListener = None
    }

    def reset(): Unit = previousText = None

  }

}
