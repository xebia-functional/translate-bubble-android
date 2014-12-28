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
import android.widget.Toast
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.clipboard.GetTextClipboardRequest
import com.fortysevendeg.translatebubble.modules.notifications.ShowTextTranslatedRequest
import com.fortysevendeg.translatebubble.modules.persistent.GetLanguagesRequest
import com.fortysevendeg.translatebubble.modules.translate.TranslateRequest
import com.fortysevendeg.translatebubble.ui.components.{ActionsView, BubbleView, ContentView}
import com.fortysevendeg.translatebubble.utils.TranslateUIType
import macroid.FullDsl._
import macroid.{AppContext, Ui}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}

class BubbleService
    extends Service
    with AppContextProvider
    with ComponentRegistryImpl {

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
    def onTouch(v: View, event: MotionEvent): Boolean = {
      val x = event.getRawX
      val y = event.getRawY
      event.getAction match {
        case MotionEvent.ACTION_DOWN =>
          initialX = paramsBubble.x
          initialY = paramsBubble.y
          initialTouchX = x
          initialTouchY = y
          true
        case MotionEvent.ACTION_CANCEL =>
          actionsView.hide()
          false
        case MotionEvent.ACTION_UP =>
          actionsView.hide()
          if (paramsBubble.x > initialX - touchSlop && paramsBubble.x < initialX + touchSlop
              && paramsBubble.y > initialY - touchSlop && paramsBubble.y < initialY + touchSlop) {
            bubbleStatus = BubbleStatus.CONTENT
            bubble.hide()
            contentView.show()
          } else {
            if (actionsView.isOverCloseView(x, y)) {
              bubble.close(paramsBubble, windowManager)
            } else if (actionsView.isOverDisableView(x, y)) {
              persistentServices.disableTranslation()
              bubble.close(paramsBubble, windowManager)
            } else if (actionsView.isOver30minView(x, y)) {
              persistentServices.disable30MinutesTranslation()
              bubble.close(paramsBubble, windowManager)
            } else {
              bubble.drop(paramsBubble, windowManager)
            }
          }
          true
        case MotionEvent.ACTION_MOVE =>
          if (!actionsView.isVisible()) {
            actionsView.show()
          }
          if (actionsView.isOverCloseView(x, y)) {
            val pos = actionsView.getClosePosition()
            paramsBubble.x = pos._1 - (bubble.getWidth / 2)
            paramsBubble.y = pos._2 - (bubble.getHeight / 2)
          } else if (actionsView.isOverDisableView(x, y)) {
            val pos = actionsView.getDisablePosition()
            paramsBubble.x = pos._1 - (bubble.getWidth / 2)
            paramsBubble.y = pos._2 - (bubble.getHeight / 2)
          } else if (actionsView.isOver30minView(x, y)) {
            val pos = actionsView.get30minPosition()
            paramsBubble.x = pos._1 - (bubble.getWidth / 2)
            paramsBubble.y = pos._2 - (bubble.getHeight / 2)
          } else {
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
          true
        case _ => false
      }
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
    def verifyMoving(x: Float, y: Float) = {
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
    val paramsContentView = new WindowManager.LayoutParams(
      widthScreen,
      getResources.getDimension(R.dimen.height_content).toInt,
      TYPE_SYSTEM_ALERT, FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT)
    paramsContentView.y = heightScreen / 2 // TODO Calculate better position y
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
    def onPrimaryClipChanged() {
      if (persistentServices.isTranslationEnable()) {
        onStartTranslate()
      }
    }
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      val restartAlarmInterval: Int = 5 * 60 * 1000
      val resetAlarmTimer: Int = 2 * 60 * 1000
      val restartIntent: Intent = new Intent(this, classOf[BubbleService])
      restartIntent.putExtra("ALARM_RESTART_SERVICE_DIED", true)
      val alarmMgr: AlarmManager = getSystemService(Context.ALARM_SERVICE).asInstanceOf[AlarmManager]
      val restartServiceHandler: Handler = new Handler {
        override def handleMessage(msg: Message) {
          val pintent: PendingIntent = PendingIntent.getService(getApplicationContext, 0, restartIntent, 0)
          alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime + restartAlarmInterval, pintent)
          sendEmptyMessageDelayed(0, resetAlarmTimer)
        }
      }
      restartServiceHandler.sendEmptyMessageDelayed(0, 0)
    }
  }

  private def onStartTranslate() {
    val typeTranslateUI = persistentServices.getTypeTranslateUI()
    if (typeTranslateUI == TranslateUIType.BUBBLE) {
      if (bubbleStatus == BubbleStatus.FLOATING) {
        bubble.show(paramsBubble, windowManager)
      } else {
        contentView.setTexts(getString(R.string.translating), "")
      }
    } else if (typeTranslateUI == TranslateUIType.NOTIFICATION) {
      notificationsServices.translating()
    }

    val result = for {
      textResponse <- clipboardServices.getText(GetTextClipboardRequest())
      persistentResponse <- persistentServices.getLanguages(GetLanguagesRequest())
      translateResponse <- translateServices.translate(
        TranslateRequest(text = textResponse.text, from = persistentResponse.from, to = persistentResponse.to)
      )
    } yield (textResponse.text, translateResponse.translated)
    result.mapUi(texts => onEndTranslate(texts._1, texts._2)).recover {
      case _ => translatedFailed()
    }

  }

  private def onEndTranslate(maybeOriginalText: Option[String], maybeTranslatedText: Option[String]) = {
    val typeTranslateUI = persistentServices.getTypeTranslateUI()
    for {
      originalText <- maybeOriginalText
      translatedText <- maybeTranslatedText
    } yield {
      if (typeTranslateUI == TranslateUIType.BUBBLE) {
        contentView.setTexts(originalText, translatedText)
        if (bubbleStatus == BubbleStatus.FLOATING) {
          bubble.stopAnimation()
        }
      } else if (typeTranslateUI == TranslateUIType.NOTIFICATION) {
        notificationsServices.showTextTranslated(ShowTextTranslatedRequest(originalText, translatedText))
      }
    }
  }

  private def translatedFailed() {
    val typeTranslateUI = persistentServices.getTypeTranslateUI()
    if (typeTranslateUI == TranslateUIType.BUBBLE) {
      contentView.setTexts(getString(R.string.failedTitle), getString(R.string.failedMessage))
      if (bubbleStatus == BubbleStatus.FLOATING) {
        bubble.stopAnimation()
      }
    } else if (typeTranslateUI == TranslateUIType.NOTIFICATION) {
      notificationsServices.failed()
    }
  }

  override def onBind(intent: Intent): IBinder = null

  override def onConfigurationChanged(newConfig: Configuration): Unit = {
    super.onConfigurationChanged(newConfig)
    reloadSizeDisplay()
    bubble.changePositionIfIsNecessary(paramsBubble, windowManager)
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
