package com.fortysevendeg.translatebubble.receivers

import android.content.{Intent, Context, BroadcastReceiver}
import com.fortysevendeg.translatebubble.ui.bubbleservice.BubbleService
import macroid.AppContext

class BootReceiver extends BroadcastReceiver {
  override def onReceive(context: Context, intent: Intent): Unit = {
    implicit lazy val appCtx = AppContext(context.getApplicationContext)
    BubbleService.launchIfIsNecessary
  }

}
