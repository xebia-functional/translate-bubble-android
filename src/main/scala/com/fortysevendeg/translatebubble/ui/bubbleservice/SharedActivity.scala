package com.fortysevendeg.translatebubble.ui.bubbleservice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.clipboard.CopyToClipboardRequest
import macroid.{AppContext, Contexts}

class SharedActivity
  extends Activity
  with Contexts[Activity]
  with ComponentRegistryImpl
  with AppContextProvider {

  private[this] val acceptedType = "text/plain"

  override implicit lazy val appContextProvider: AppContext = activityAppContext

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
      BubbleService.launchIfIsNecessary()
      clipboardServices.copyToClipboard(CopyToClipboardRequest(text))
    }

}