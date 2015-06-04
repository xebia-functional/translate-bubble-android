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

    val intent = getIntent
    (Option(intent.getAction), Option(intent.getType)) match {
      case (Some(a), Some(t)) if a == Intent.ACTION_SEND && t == acceptedType =>
        handleText(Option(intent.getStringExtra(Intent.EXTRA_TEXT)))
    }

    finish()
  }

  private def handleText(maybeString: Option[String]) =
    maybeString match {
      case Some(text) if clipboardServices.isValidText(text) =>
        persistentServices.enableTranslation()
        BubbleService.launchIfIsNecessary()
        clipboardServices.copyToClipboard(CopyToClipboardRequest(text))
    }

}