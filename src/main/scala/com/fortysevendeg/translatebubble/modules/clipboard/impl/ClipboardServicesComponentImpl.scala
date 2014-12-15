package com.fortysevendeg.translatebubble.modules.clipboard.impl

import android.content.{ClipData, Context, ClipboardManager}
import com.fortysevendeg.translatebubble.modules.clipboard._
import com.fortysevendeg.translatebubble.service._
import macroid.AppContext
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait ClipboardServicesComponentImpl
    extends ClipboardServicesComponent {

  def clipboardServices(implicit appContext: AppContext) = new ClipboardServicesImpl

  class ClipboardServicesImpl(implicit appContext: AppContext) extends ClipboardServices {

    var previousText: String = null

    var clipChangedListener: Option[ClipboardManager.OnPrimaryClipChangedListener] = None

    val clipboardManager: ClipboardManager = {
      appContext.get.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
    }

    override def getText: Service[GetTextClipboardRequest, GetTextClipboardResponse] = {
      request =>
        Future {
          var result: Option[String] = None
          val clip: ClipData = clipboardManager.getPrimaryClip
          if (clip != null && clip.getItemCount > 0) {
            val aux: CharSequence = clip.getItemAt(0).getText
            if (aux != null && aux.length > 0 && !(aux == previousText)) {
              previousText = aux.toString
              result = Some(previousText)
            }
          }
          GetTextClipboardResponse(result)
        }
    }

    override def copyToClipboard: Service[CopyToClipboardRequest, CopyToClipboardResponse] = {
      request =>
        Future {
          val clip = ClipData.newPlainText("label", request.text)
          clipboardManager.setPrimaryClip(clip)
          CopyToClipboardResponse()
        }
    }

    def init(listener: ClipboardManager.OnPrimaryClipChangedListener) = {
      if (clipChangedListener.isDefined) {
        clipChangedListener map clipboardManager.removePrimaryClipChangedListener
      }
      clipChangedListener = Some(listener)
      clipboardManager.addPrimaryClipChangedListener(listener)
    }

    def destroy() = {
      clipChangedListener map clipboardManager.removePrimaryClipChangedListener
      clipChangedListener = None
    }

    def reset() {
      previousText = null
    }

  }

}
