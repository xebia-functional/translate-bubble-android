package com.fortysevendeg.translatebubble.ui.bubbleservice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.clipboard.CopyToClipboardRequest
import macroid.{ContextWrapper, Contexts}

class SharedActivity
  extends Activity
  with Contexts[Activity]
  with ComponentRegistryImpl {

  private[this] val acceptedType = "text/plain"

  override lazy val contextProvider: ContextWrapper = activityContextWrapper

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    readTextIntent map handleText

    finish()
  }

  private[this] def readTextIntent: Option[String] =
    for {
      intent <- Option(getIntent)
      action <- Option(intent.getAction) if action == Intent.ACTION_SEND
      contentType <- Option(intent.getType) if contentType == `acceptedType`
      text <- Option(intent.getStringExtra(Intent.EXTRA_TEXT))
    } yield text

  private[this] def handleText(text: String) =
    if (clipboardServices.isValidText(text)) {
      persistentServices.enableTranslation()
      BubbleService.launchIfIsNecessary(this)
      clipboardServices.copyToClipboard(CopyToClipboardRequest(text))
    }

}