package com.fortysevendeg.translatebubble.ui.bubbleservice

import android.app.Service
import android.content.Context
import android.graphics.{PixelFormat, Point}
import android.support.v4.view.ViewConfigurationCompat
import android.view.ViewGroup.LayoutParams._
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams._
import android.view._
import com.fortysevendeg.macroid.extras.DeviceMediaQueries._
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.analytics.AnalyticsServicesComponent
import com.fortysevendeg.translatebubble.modules.clipboard.ClipboardServicesComponent
import com.fortysevendeg.translatebubble.modules.notifications.{NotificationsServicesComponent, ShowTextTranslatedRequest}
import com.fortysevendeg.translatebubble.modules.persistent.PersistentServicesComponent
import com.fortysevendeg.translatebubble.ui.commons.Strings._
import com.fortysevendeg.translatebubble.ui.components.{ActionsView, BubbleView, ContentView}
import com.fortysevendeg.translatebubble.utils.TranslateUIType
import macroid.FullDsl._
import macroid.{Contexts, Ui}

trait Composer {

  self: Service
    with Contexts[Service]
    with AnalyticsServicesComponent
    with PersistentServicesComponent
    with ClipboardServicesComponent
    with NotificationsServicesComponent =>

  protected var widthScreen: Int = 0

  protected var heightScreen: Int = 0

  object BubbleStatus extends Enumeration {
    type BubbleStatus = Value
    val FLOATING, CONTENT = Value
  }

  protected var bubbleStatus = BubbleStatus.FLOATING

  protected lazy val configuration: ViewConfiguration = ViewConfiguration.get(getApplicationContext)

  protected def reloadSizeDisplay(): Ui[_] = Ui {
    val display: Display = windowManager.getDefaultDisplay
    val size: Point = new Point
    display.getSize(size)
    widthScreen = size.x
    heightScreen = size.y
    bubble.setDimensionsScreen(widthScreen, heightScreen)
  }

  // Distance in pixels a touch can wander before we think the user is scrolling
  lazy val touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration)

  protected lazy val windowManager: WindowManager = getSystemService(Context.WINDOW_SERVICE).asInstanceOf[WindowManager]

  protected lazy val (bubble: BubbleView, paramsBubble: LayoutParams) = {
    val bubble = new BubbleView(this)
    runUi(bubble.hide)
    val paramsBubble = new WindowManager.LayoutParams(
      WRAP_CONTENT,
      WRAP_CONTENT,
      TYPE_SYSTEM_ALERT, FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT)
    paramsBubble.gravity = Gravity.TOP | Gravity.LEFT
    bubble.init(paramsBubble)
    (bubble, paramsBubble)
  }

  protected lazy val (contentView: ContentView, paramsContentView: LayoutParams) = {
    val contentView = new ContentView(this)
    runUi(contentView.hide())
    val width = {
      val w = if (widthScreen > heightScreen) heightScreen else widthScreen
      if (tablet) (w * 0.7f).toInt else w
    }
    val paramsContentView = new WindowManager.LayoutParams(
      width,
      getResources.getDimension(R.dimen.height_content).toInt,
      TYPE_SYSTEM_ALERT, FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT)
    paramsContentView.y = heightScreen - getResources.getDimension(R.dimen.height_content).toInt
    paramsContentView.gravity = Gravity.TOP | Gravity.LEFT
    (contentView, paramsContentView)
  }

  protected lazy val (actionsView: ActionsView, paramsActionsView: LayoutParams) = {
    val actionsView = new ActionsView(this)
    runUi(actionsView.gone)
    val paramsActionsView: WindowManager.LayoutParams = new WindowManager.LayoutParams(
      MATCH_PARENT,
      MATCH_PARENT,
      TYPE_SYSTEM_ALERT,
      FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT)
    (actionsView, paramsActionsView)
  }

  protected val bubbleTouchListener = new View.OnTouchListener {
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
          runUi(actionsView.hide())
          moving = false
          false
        case MotionEvent.ACTION_UP =>
          runUi(actionsView.hide())
          val ui = actionsView match {
            // Bubble didn't move, we show text translated
            case `actionsView` if (!moving && paramsBubble.x > initialX - touchSlop && paramsBubble.x < initialX + touchSlop
              && paramsBubble.y > initialY - touchSlop && paramsBubble.y < initialY + touchSlop) =>
              bubbleStatus = BubbleStatus.CONTENT
              bubble.hide ~ contentView.show()
            // Bubble was moved over CloseView
            case `actionsView` if actionsView.isOverCloseView(x, y) =>
              bubble.hideFromCloseAction(paramsBubble, windowManager)
            // Bubble was moved over DisableTranslation
            case `actionsView` if actionsView.isOverDisableView(x, y) =>
              analyticsServices.send(
                analyticsTranslateService,
                Some(analyticsDisable))
              persistentServices.disableTranslation()
              bubble.hideFromOptionAction(paramsBubble, windowManager)
            // Bubble was moved over DisableTranslation during 30 minutes
            case `actionsView` if actionsView.isOver30minView(x, y) =>
              analyticsServices.send(
                analyticsTranslateService,
                Some(analytics30MinDisable))
              persistentServices.disable30MinutesTranslation()
              bubble.hideFromOptionAction(paramsBubble, windowManager)
            // Bubble drops somewhere else
            case _ => bubble.drop(paramsBubble, windowManager)
          }
          runUi(ui)
          moving = false
          true
        case MotionEvent.ACTION_MOVE =>
          if (moving) {
            runUi(actionsView.show())
            actionsView match {
              // Bubble is over CloseView
              case `actionsView` if actionsView.isOverCloseView(x, y) =>
                val pos = actionsView.getClosePosition
                paramsBubble.x = pos._1 - (bubble.getWidth / 2)
                paramsBubble.y = pos._2 - (bubble.getHeight / 2)
              // Bubble is over DisableTranslation
              case `actionsView` if actionsView.isOverDisableView(x, y) =>
                val pos = actionsView.getDisablePosition
                paramsBubble.x = pos._1 - (bubble.getWidth / 2)
                paramsBubble.y = pos._2 - (bubble.getHeight / 2)
              // Bubble is over DisableTranslation30min
              case `actionsView` if actionsView.isOver30minView(x, y) =>
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

  protected val contentTouchListener = new View.OnTouchListener {
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

  protected def initializeUi: Ui[_] = reloadSizeDisplay() ~
    Ui {
      windowManager.addView(actionsView, paramsActionsView)

      bubble.setOnTouchListener(bubbleTouchListener)
      windowManager.addView(bubble, paramsBubble)

      contentView.setOnTouchListener(contentTouchListener)
      windowManager.addView(contentView, paramsContentView)
    } ~
    (contentView.options <~ On.click(collapse()))

  protected def destroyUi: Ui[_] = Ui {
    if (bubble != null) windowManager.removeView(bubble)
    if (contentView != null) windowManager.removeView(contentView)
    if (actionsView != null) windowManager.removeView(actionsView)
  }

  protected def configurationChanged: Ui[_]  = reloadSizeDisplay() ~
    bubble.changePositionIfIsNecessary(paramsBubble, windowManager) ~
    contentView.changePositionIfIsNecessary(widthScreen, heightScreen, paramsContentView, windowManager)

  protected def loading: Ui[_] = {
    val typeTranslateUI = persistentServices.getTypeTranslateUI()
    if (typeTranslateUI == TranslateUIType.BUBBLE) {
      val ui = bubbleStatus match {
        case BubbleStatus.FLOATING => bubble.show(paramsBubble, windowManager)
        case BubbleStatus.CONTENT => bubble.startAnimation()
      }
      ui ~ contentView.setTexts(getString(R.string.translating), "-", "-")
    } else Ui.nop
  }

  protected def translatedSuccess(
    originalText: String,
    translatedText: String,
    label: String
    ): Ui[_] = {
    val typeTranslateUI = persistentServices.getTypeTranslateUI()

    analyticsServices.send(
      analyticsTranslateService,
      Some(typeTranslateUI.toString),
      Some(analyticsClipboard),
      Some(label))

    persistentServices.getLanguagesString map {
      languages =>
        typeTranslateUI match {
          case TranslateUIType.BUBBLE =>
            contentView.setTexts(languages, originalText, translatedText) ~
              bubble.stopAnimation()
          case TranslateUIType.NOTIFICATION =>
            Ui {
              notificationsServices.showTextTranslated(ShowTextTranslatedRequest(originalText, translatedText))
            }
        }
    } getOrElse Ui.nop
  }

  protected def translatedFailed(): Ui[_] = persistentServices.getTypeTranslateUI() match {
    case TranslateUIType.BUBBLE =>
      contentView.setTexts(getString(R.string.failedTitle), getString(R.string.failedMessage), "") ~
        bubble.stopAnimation()
    case TranslateUIType.NOTIFICATION =>
      Ui {
        notificationsServices.failed()
      }
  }

  protected def collapse(): Ui[_] = {
    bubbleStatus = BubbleStatus.FLOATING
    contentView.collapse(paramsContentView, windowManager) ~
      bubble.show(paramsBubble, windowManager) ~
      bubble.stopAnimation()
  }

}
