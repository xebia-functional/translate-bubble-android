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
import android.graphics.{PixelFormat, Point}
import android.os._
import android.support.v4.view.ViewConfigurationCompat
import android.view.ViewGroup.LayoutParams._
import android.view.WindowManager.LayoutParams._
import android.view._
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.macroid.extras.DeviceMediaQueries._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.clipboard.{GetTextClipboardRequest, GetTextClipboardResponse}
import com.fortysevendeg.translatebubble.modules.notifications.ShowTextTranslatedRequest
import com.fortysevendeg.translatebubble.modules.persistent.{GetLanguagesRequest, GetLanguagesResponse}
import com.fortysevendeg.translatebubble.modules.translate.{TranslateRequest, TranslateResponse}
import com.fortysevendeg.translatebubble.ui.commons.Strings._
import com.fortysevendeg.translatebubble.ui.components.{ActionsView, BubbleView, ContentView}
import com.fortysevendeg.translatebubble.utils.{OptionOps, TranslateUIType}
import macroid.FullDsl._
import macroid.{AppContext, Ui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}

class BubbleService
    extends Service
    with AppContextProvider
    with ComponentRegistryImpl
    with OptionOps {

  override implicit lazy val appContextProvider = AppContext(getApplicationContext)

  var widthScreen: Int = 0

  var heightScreen: Int = 0

  object BubbleStatus extends Enumeration {
    type BubbleStatus = Value
    val FLOATING, CONTENT = Value
  }

  private var bubbleStatus = BubbleStatus.FLOATING

  lazy val configuration: ViewConfiguration = ViewConfiguration.get(getApplicationContext)

  // Distance in pixels a touch can wander before we think the user is scrolling
  lazy val touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)

  private val bubbleTouchListener = new View.OnTouchListener {
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f
    private var moving = false
    def onTouch(v: View, event: MotionEvent): Boolean = {
      val x = event.getRawX
      val y = event.getRawY
      event.getAction match {
        case MotionEvent.ACTION_DOWN =>
          initialX = paramsBubble.x
          initialY = paramsBubble.y
          initialTouchX = x
          initialTouchY = y
          moving = false
          true
        case MotionEvent.ACTION_CANCEL =>
          actionsView.hide()
          moving = false
          false
        case MotionEvent.ACTION_UP =>
          actionsView.hide()
          actionsView match {
            // Bubble didn't move, we show text translated
            case actionView if (!moving && paramsBubble.x > initialX - touchSlop && paramsBubble.x < initialX + touchSlop
                && paramsBubble.y > initialY - touchSlop && paramsBubble.y < initialY + touchSlop) =>
              bubbleStatus = BubbleStatus.CONTENT
              bubble.hide()
              contentView.show()
            // Bubble was moved over CloseView
            case actionsView if actionsView.isOverCloseView(x, y) =>
              bubble.hideFromCloseAction(paramsBubble, windowManager)
            // Bubble was moved over DisableTranslation
            case actionsView if actionsView.isOverDisableView(x, y) =>
              analyticsServices.send(
                analyticsTranslateService,
                Some(analyticsDisable))
              persistentServices.disableTranslation()
              bubble.hideFromOptionAction(paramsBubble, windowManager)
            // Bubble was moved over DisableTranslation during 30 minutes
            case actionsView if actionsView.isOver30minView(x, y) =>
              analyticsServices.send(
                analyticsTranslateService,
                Some(analytics30MinDisable))
              persistentServices.disable30MinutesTranslation()
              bubble.hideFromOptionAction(paramsBubble, windowManager)
            // Bubble drops somewhere else
            case _ => bubble.drop(paramsBubble, windowManager)
          }
          moving = false
          true
        case MotionEvent.ACTION_MOVE =>
          if (moving) {
            if (!actionsView.isVisible) {
              actionsView.show()
            }
            actionsView match {
              // Bubble is over CloseView
              case actionsView if actionsView.isOverCloseView(x, y) =>
                val pos = actionsView.getClosePosition
                paramsBubble.x = pos._1 - (bubble.getWidth / 2)
                paramsBubble.y = pos._2 - (bubble.getHeight / 2)
              // Bubble is over DisableTranslation
              case actionsView if actionsView.isOverDisableView(x, y) =>
                val pos = actionsView.getDisablePosition
                paramsBubble.x = pos._1 - (bubble.getWidth / 2)
                paramsBubble.y = pos._2 - (bubble.getHeight / 2)
              // Bubble is over DisableTranslation30min
              case actionsView if actionsView.isOver30minView(x, y) =>
                val pos = actionsView.get30minPosition()
                paramsBubble.x = pos._1 - (bubble.getWidth / 2)
                paramsBubble.y = pos._2 - (bubble.getHeight / 2)
              // Bubble is moving somewhere else
              case _ =>
                val newPosX = initialX + (x - initialTouchX).toInt
                val newPosY = initialY + (y - initialTouchY).toInt
                paramsBubble.x = newPosX
                paramsBubble.y = newPosY match {
                  case _ if newPosY < 0 =>
                    0
                  case _ if newPosY > heightScreen - bubble.getHeight =>
                    heightScreen - bubble.getHeight
                  case _ =>
                    newPosY
                }
            }
            windowManager.updateViewLayout(bubble, paramsBubble)
          } else {
            val (xMoved, yMoved) = verifyMoving(x, y)
            moving = xMoved || yMoved
          }
          true
        case _ => false
      }
    }
    def verifyMoving(x: Float, y: Float): (Boolean, Boolean) = {
      val xDiff: Int = Math.abs(x - initialTouchX).toInt
      val yDiff: Int = Math.abs(y - initialTouchY).toInt
      (xDiff > touchSlop, yDiff > touchSlop)
    }
  }

  private val contentTouchListener = new View.OnTouchListener {
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f
    private var moving = false
    def onTouch(v: View, event: MotionEvent): Boolean = {
      val x = event.getRawX
      val y = event.getRawY
      event.getAction match {
        case MotionEvent.ACTION_DOWN =>
          initialX = paramsContentView.x
          initialY = paramsContentView.y
          initialTouchX = x
          initialTouchY = y
          moving = false
          true
        case MotionEvent.ACTION_CANCEL =>
          paramsContentView.alpha = 1f
          windowManager.updateViewLayout(contentView, paramsContentView)
          moving = false
          false
        case MotionEvent.ACTION_UP =>
          paramsContentView.alpha = 1f
          windowManager.updateViewLayout(contentView, paramsContentView)
          moving = false
          true
        case MotionEvent.ACTION_MOVE =>
          if (moving) {
            val newPosX = initialX + (x - initialTouchX).toInt
            val newPosY = initialY + (y - initialTouchY).toInt
            paramsContentView.x = newPosX match {
              case _ if newPosX < 0 =>
                0
              case _ if newPosX > widthScreen - paramsContentView.width =>
                widthScreen - paramsContentView.width
              case _ =>
                newPosX
            }
            paramsContentView.y = newPosY match {
              case _ if newPosY < 0 =>
                0
              case _ if newPosY > heightScreen - paramsContentView.height =>
                heightScreen - paramsContentView.height
              case _ =>
                newPosY
            }
            windowManager.updateViewLayout(contentView, paramsContentView)
          } else {
            val (xMoved, yMoved) = verifyMoving(x, y)
            moving = xMoved || yMoved
            if (moving) {
              // start movement
              paramsContentView.alpha = 0.6f
              windowManager.updateViewLayout(contentView, paramsContentView)
            }
          }
          true
        case _ => false
      }
    }
    def verifyMoving(x: Float, y: Float): (Boolean, Boolean) = {
      val xDiff: Int = Math.abs(x - initialTouchX).toInt
      val yDiff: Int = Math.abs(y - initialTouchY).toInt
      (xDiff > touchSlop, yDiff > touchSlop)
    }
  }

  private lazy val windowManager: WindowManager = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]

  private def reloadSizeDisplay() = {
    val display: Display = windowManager.getDefaultDisplay
    val size: Point = new Point
    display.getSize(size)
    widthScreen = size.x
    heightScreen = size.y
    bubble.setDimensionsScreen(widthScreen, heightScreen)
  }

  private lazy val (bubble, paramsBubble) = {
    val bubble = new BubbleView(this)
    bubble.hide()
    val paramsBubble = new WindowManager.LayoutParams(
      WRAP_CONTENT,
      WRAP_CONTENT,
      TYPE_SYSTEM_ALERT, FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT)
    paramsBubble.gravity = Gravity.TOP | Gravity.LEFT
    bubble.init(paramsBubble)
    (bubble, paramsBubble)
  }

  private lazy val (contentView, paramsContentView) = {
    val contentView = new ContentView(this)
    contentView.hide()
    val width = {
      val w = if (widthScreen > heightScreen) heightScreen else widthScreen
      if (tablet) (w * 0.7f).toInt else w
    }
    val paramsContentView = new WindowManager.LayoutParams(
      width,
      getResources.getDimension(R.dimen.height_content).toInt,
      TYPE_SYSTEM_ALERT, FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT)
    paramsContentView.y = heightScreen - appContextProvider.get.getResources.getDimension(R.dimen.height_content).toInt
    paramsContentView.gravity = Gravity.TOP | Gravity.LEFT
    (contentView, paramsContentView)
  }

  private lazy val (actionsView, paramsActionsView) = {
    val actionsView = new ActionsView(this)
    actionsView.hide()
    val paramsActionsView: WindowManager.LayoutParams = new WindowManager.LayoutParams(
      MATCH_PARENT,
      MATCH_PARENT,
      TYPE_SYSTEM_ALERT,
      FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT)
    (actionsView, paramsActionsView)
  }

  private val clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener {
    def onPrimaryClipChanged() = if (persistentServices.isTranslationEnable()) onStartTranslate()
  }

  override def onCreate() {
    super.onCreate()

    reloadSizeDisplay()

    clipboardServices.init(clipChangedListener)

    windowManager.addView(actionsView, paramsActionsView)

    bubble.setOnTouchListener(bubbleTouchListener)
    windowManager.addView(bubble, paramsBubble)

    contentView.setOnTouchListener(contentTouchListener)
    windowManager.addView(contentView, paramsContentView)
    runUi(
      contentView.options <~ On.click {
        Ui(collapse())
      }
    )
  }

  private def close() {
    bubbleStatus = BubbleStatus.FLOATING
    contentView.hide()
    bubble.hide()
  }

  private def collapse() {
    bubbleStatus = BubbleStatus.FLOATING
    contentView.collapse(paramsContentView, windowManager)
    bubble.show(paramsBubble, windowManager)
    bubble.stopAnimation()
  }

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    ensureServiceStaysRunning()
    Service.START_STICKY
  }

  override def onDestroy() {
    super.onDestroy()
    clipboardServices.destroy()
    if (bubble != null) windowManager.removeView(bubble)
    if (contentView != null) windowManager.removeView(contentView)
    if (actionsView != null) windowManager.removeView(actionsView)
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

  private def onStartTranslate() {
    val typeTranslateUI = persistentServices.getTypeTranslateUI()
    if (typeTranslateUI == TranslateUIType.BUBBLE) {
      bubbleStatus match {
        case BubbleStatus.FLOATING => bubble.show(paramsBubble, windowManager)
        case BubbleStatus.CONTENT => bubble.startAnimation()
      }
      contentView.setTexts(getString(R.string.translating), "-", "-")
    }

    val result = for {
      GetTextClipboardResponse(Some(text)) <- clipboardServices.getText(GetTextClipboardRequest())
      GetLanguagesResponse(from, to) <- persistentServices.getLanguages(GetLanguagesRequest())
      TranslateResponse(Some(translatedText)) <- translateServices.translate(
        TranslateRequest(text = text, from = from, to = to))
    } yield (text, translatedText, "%s-%s".format(from.toString, to.toString))

    result mapUi {
      case (text: String, translated: String, langs: String) => onEndTranslate(text, translated, langs)
      case _ => translatedFailed()
    }
  }

  private def onEndTranslate(
      originalText: String,
      translatedText: String,
      label: String) = {
    val typeTranslateUI = persistentServices.getTypeTranslateUI()

    analyticsServices.send(
      analyticsTranslateService,
      Some(typeTranslateUI.toString),
      Some(analyticsClipboard),
      Some(label))

    persistentServices.getLanguagesString foreach {
      languages =>
        typeTranslateUI match {
          case TranslateUIType.BUBBLE =>
            contentView.setTexts(languages, originalText, translatedText)
            bubble.stopAnimation()
          case TranslateUIType.NOTIFICATION =>
            notificationsServices.showTextTranslated(ShowTextTranslatedRequest(originalText, translatedText))
        }
    }
  }

  private def translatedFailed() = {
    val typeTranslateUI = persistentServices.getTypeTranslateUI()
    typeTranslateUI match {
      case TranslateUIType.BUBBLE =>
        contentView.setTexts(getString(R.string.failedTitle), getString(R.string.failedMessage), "")
        bubble.stopAnimation()
      case TranslateUIType.NOTIFICATION =>
        notificationsServices.failed()
    }
  }

  override def onBind(intent: Intent): IBinder = null

  override def onConfigurationChanged(newConfig: Configuration): Unit = {
    super.onConfigurationChanged(newConfig)
    reloadSizeDisplay()
    bubble.changePositionIfIsNecessary(paramsBubble, windowManager)
    contentView.changePositionIfIsNecessary(widthScreen, heightScreen, paramsContentView, windowManager)
  }

}

object BubbleService {

  def launchIfIsNecessary()(implicit appContext: AppContext) {
    Try(appContext.get.startService(new Intent(appContext.get, classOf[BubbleService]))) match {
      case Failure(ex) => ex.printStackTrace()
      case _ =>
    }
  }

}
