package com.fortysevendeg.translatebubble.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import macroid.AppContext

class RestartTranslationService
    extends Service
    with ComponentRegistryImpl {

  override implicit lazy val appContextProvider = AppContext(getApplicationContext)

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int =
    super.onStartCommand(intent, flags, startId)

  override def onCreate(): Unit = {
    super.onCreate()
    persistentServices.enableTranslation()
    Log.d("TranslateApp", "enabling")
  }

  override def onBind(intent: Intent): IBinder = null

}
