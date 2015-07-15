/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortysevendeg.translatebubble.ui.bubbleservice

import android.app.{AlarmManager, PendingIntent, Service}
import android.content.res.Configuration
import android.content.{ClipboardManager, Context, Intent}
import android.os._
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.clipboard.{GetTextClipboardRequest, GetTextClipboardResponse}
import com.fortysevendeg.translatebubble.modules.persistent.{GetLanguagesRequest, GetLanguagesResponse}
import com.fortysevendeg.translatebubble.modules.translate.{TranslateRequest, TranslateResponse}
import macroid.FullDsl._
import macroid.{ContextWrapper, Contexts, Ui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}

class BubbleService
  extends Service
  with Contexts[Service]
  with ComponentRegistryImpl
  with Composer {

  override lazy val contextProvider: ContextWrapper = serviceContextWrapper

  private val clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener {
    def onPrimaryClipChanged() =
      if (persistentServices.isTranslationEnable() && clipboardServices.isValidCall) runUi(startTranslate())
  }

  override def onCreate() = {
    super.onCreate()
    clipboardServices.init(clipChangedListener)
    runUi(initializeUi)
  }

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    ensureServiceStaysRunning()
    Service.START_STICKY
  }

  override def onConfigurationChanged(newConfig: Configuration): Unit = {
    super.onConfigurationChanged(newConfig)
    runUi(configurationChanged)
  }

  override def onDestroy() {
    super.onDestroy()
    clipboardServices.destroy()
    runUi(destroyUi)
  }

  private def startTranslate(): Ui[_] = {
    val result = for {
      GetTextClipboardResponse(Some(text)) <- clipboardServices.getText(GetTextClipboardRequest())
      GetLanguagesResponse(from, to) <- persistentServices.getLanguages(GetLanguagesRequest())
      TranslateResponse(Some(translatedText)) <- translateServices.translate(
        TranslateRequest(text = text, from = from, to = to))
    } yield (text, translatedText, s"${from.toString}-${to.toString}")

    result mapUi {
      case (text: String, translated: String, langs: String) => translatedSuccess(text, translated, langs)
      case _ => translatedFailed()
    } recoverUi {
      case _ => translatedFailed()
    }
    loading
  }

  private def ensureServiceStaysRunning() {
    // We have detected a KitKat bug that sometimes the service falls. We're trying to fix this bug here
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
      val restartAlarmInterval: Int = 5 * 60 * 1000
      val resetAlarmTimer: Int = 2 * 60 * 1000
      val restartIntent: Intent = new Intent(this, classOf[BubbleService])
      restartIntent.putExtra("ALARM_RESTART_SERVICE_DIED", true)
      val alarmMgr: AlarmManager = getSystemService(Context.ALARM_SERVICE).asInstanceOf[AlarmManager]
      val restartServiceHandler: Handler = new Handler {
        override def handleMessage(msg: Message) {
          val pendingIntent: PendingIntent = PendingIntent.getService(getApplicationContext, 0, restartIntent, 0)
          alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime + restartAlarmInterval, pendingIntent)
          sendEmptyMessageDelayed(0, resetAlarmTimer)
        }
      }
      restartServiceHandler.sendEmptyMessageDelayed(0, 0)
    }
  }

  override def onBind(intent: Intent): IBinder = null

}

object BubbleService {

  def launchIfIsNecessary(context: Context) = Try(context.startService(new Intent(context, classOf[BubbleService]))) match {
    case Failure(ex) => ex.printStackTrace()
    case _ =>
  }

}
