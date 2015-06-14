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

  private val acceptedType = "text/plain"

  override implicit lazy val appContextProvider: AppContext = activityAppContext

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    Option(getIntent) map { i =>
      (Option(i.getAction), Option(i.getType)) match {
        case (Some(Intent.ACTION_SEND), Some(`acceptedType`)) =>
          Option(i.getStringExtra(Intent.EXTRA_TEXT)) map handleText
      }
    }

    finish()
  }

  private def handleText(text: String) =
    if (clipboardServices.isValidText(text)) {
      persistentServices.enableTranslation()
      BubbleService.launchIfIsNecessary()
      clipboardServices.copyToClipboard(CopyToClipboardRequest(text))
    }

}