package com.fortysevendeg.translatebubble.ui.bubbleservice

import android.app.{AlarmManager, PendingIntent, Service}
import android.content.{ClipboardManager, Context, Intent}
import android.graphics.{PixelFormat, Point}
import android.os._
import android.view.ViewGroup.LayoutParams._
import android.view.WindowManager.LayoutParams._
import android.view._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.macroid.AppContextProvider
import com.fortysevendeg.translatebubble.modules.ComponentRegistryImpl
import com.fortysevendeg.translatebubble.modules.clipboard.{ClipboardServicesComponent, GetTextClipboardRequest}
import com.fortysevendeg.translatebubble.modules.notifications.{NotificationsServicesComponent, ShowTextTranslatedRequest}
import com.fortysevendeg.translatebubble.modules.persistent.{GetLanguagesRequest, PersistentServicesComponent}
import com.fortysevendeg.translatebubble.modules.translate.{TranslateRequest, TranslateServicesComponent}
import com.fortysevendeg.translatebubble.ui.components.{BubbleView, CloseView, ContentView, GestureListener}
import com.fortysevendeg.translatebubble.utils.TranslateUIType
import macroid.AppContext
import macroid.FullDsl._

import scala.concurrent.ExecutionContext.Implicits.global

class BubbleService
    extends Service
    with AppContextProvider
    with ComponentRegistryImpl {

  override implicit lazy val appContextProvider = AppContext(getApplicationContext)

  object BubbleStatus extends Enumeration {
    type BubbleStatus = Value
    val FLOATING, CONTENT = Value
  }

  private var windowManager: WindowManager = null
  private var bubble: BubbleView = null
  private var contentView: ContentView = null
  private var closeView: CloseView = null
  private var paramsBubble: WindowManager.LayoutParams = null
  private var paramsContentView: WindowManager.LayoutParams = null
  private var width: Int = 0
  private var height: Int = 0
  private var bubbleStatus = BubbleStatus.FLOATING

  private val clipChangedListener: ClipboardManager.OnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener {
    def onPrimaryClipChanged() {
      if (persistentServices.isTranslationEnable()) {
        onStartTranslate()
      }
    }
  }

  private val gestureListener: GestureListener = new GestureListener {
    def onUp() {
      collapse()
    }
    def onDown() {
      close()
    }
    def onPrevious() {
    }
    def onNext() {
    }
  }

  private val touchListener: View.OnTouchListener = new View.OnTouchListener {
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f
    def onTouch(v: View, event: MotionEvent): Boolean = {
      event.getAction match {
        case MotionEvent.ACTION_DOWN =>
          initialX = paramsBubble.x
          initialY = paramsBubble.y
          initialTouchX = event.getRawX
          initialTouchY = event.getRawY
          true
        case MotionEvent.ACTION_CANCEL =>
          closeView.hide()
          false
        case MotionEvent.ACTION_UP =>
          closeView.hide()
          if (initialX == paramsBubble.x && initialY == paramsBubble.y) {
            bubbleStatus = BubbleStatus.CONTENT
            bubble.hide()
            contentView.show()
          } else {
            bubble.drop(paramsBubble, windowManager)
          }
          true
        case MotionEvent.ACTION_MOVE =>
          if (!closeView.isVisible) {
            closeView.show()
          }
          paramsBubble.x = initialX + (event.getRawX - initialTouchX).toInt
          paramsBubble.y = initialY + (event.getRawY - initialTouchY).toInt
          windowManager.updateViewLayout(bubble, paramsBubble)
          true
        case _ => false
      }
    }
  }

  override def onCreate() {
    super.onCreate()

    clipboardServices.init(clipChangedListener)

    windowManager = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]
    val display: Display = windowManager.getDefaultDisplay
    val size: Point = new Point
    display.getSize(size)
    width = size.x
    height = size.y

    closeView = new CloseView(this)
    closeView.hide()
    val heightCloseZone: Int = getResources.getDimension(R.dimen.height_close_zone).toInt
    val closeViewParams: WindowManager.LayoutParams = new WindowManager.LayoutParams(MATCH_PARENT, heightCloseZone, TYPE_PHONE, FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
    closeViewParams.gravity = Gravity.BOTTOM | Gravity.LEFT
    windowManager.addView(closeView, closeViewParams)

    bubble = new BubbleView(this)
    bubble.hide()
    bubble.setOnTouchListener(touchListener)
    paramsBubble = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, TYPE_PHONE, FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
    paramsBubble.x = 0
    paramsBubble.y = getResources.getDimension(R.dimen.bubble_start_pos_y).toInt
    paramsBubble.gravity = Gravity.TOP | Gravity.LEFT
    bubble.init(height, width)
    windowManager.addView(bubble, paramsBubble)

    contentView = new ContentView(this)
    contentView.setGestureListener(gestureListener)
    contentView.hide()
    contentView.setListeners(new View.OnClickListener {
      def onClick(v: View) {
        collapse()
      }
    }, new View.OnClickListener {
      def onClick(v: View) {
        close()
      }
    })

    paramsContentView = new WindowManager.LayoutParams(MATCH_PARENT, WRAP_CONTENT, TYPE_PHONE, FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)
    paramsContentView.gravity = Gravity.BOTTOM | Gravity.LEFT
    windowManager.addView(contentView, paramsContentView)

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
    if (closeView != null) windowManager.removeView(closeView)
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
}

object BubbleService {

  def launchIfIsNecessary()(implicit appContext: AppContext) {
    try {
      appContext.get.startService(new Intent(appContext.get, classOf[BubbleService]))
    } catch {
      case e: SecurityException => e.printStackTrace()
    }
  }

}
