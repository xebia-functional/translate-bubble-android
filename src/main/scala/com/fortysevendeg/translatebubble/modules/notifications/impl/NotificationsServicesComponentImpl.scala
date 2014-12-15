package com.fortysevendeg.translatebubble.modules.notifications.impl

import android.app.{Notification, NotificationManager, PendingIntent}
import android.content.{Context, Intent}
import android.support.v4.app.NotificationCompat
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.notifications._
import com.fortysevendeg.translatebubble.modules.persistent.impl.PersistentServicesComponentImpl
import com.fortysevendeg.translatebubble.service.Service
import com.fortysevendeg.translatebubble.ui.preferences.MainActivity
import macroid.AppContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait NotificationsServicesComponentImpl
    extends NotificationsServicesComponent
    with PersistentServicesComponentImpl { // TODO Don't user implementation

  def notificationsServices(implicit appContext: AppContext) = new NotificationsServicesImpl

  class NotificationsServicesImpl(implicit appContext: AppContext)
      extends NotificationsServices {

    private val NOTIFICATION_ID: Int = 1100

    val notifyManager = appContext.get.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

    override def showTextTranslated: Service[ShowTextTranslatedRequest, ShowTextTranslatedResponse] = {
      request =>
          Future {
            val notificationIntent: Intent = new Intent(appContext.get, classOf[MainActivity])
            val contentIntent: PendingIntent = PendingIntent.getActivity(appContext.get, getUniqueId, notificationIntent, 0)

            val builder = new NotificationCompat.Builder(appContext.get)
            val title: String = appContext.get.getString(R.string.translatedTitle, request.original)
            builder
                .setContentTitle(title)
                .setContentText(request.translated)
                .setTicker(title)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)

            if (persistentServices.isHeadsUpEnable()) {
              builder
                  .setDefaults(Notification.DEFAULT_VIBRATE)
                  .setPriority(NotificationCompat.PRIORITY_HIGH)
            }

            val notification: Notification = new NotificationCompat.BigTextStyle(builder)
                .bigText(request.translated).build

            notifyManager.notify(NOTIFICATION_ID, notification)
            ShowTextTranslatedResponse()
          }
    }

    override def translating(): Unit = {
      val notificationIntent: Intent = new Intent(appContext.get, classOf[MainActivity])
      val contentIntent: PendingIntent = PendingIntent.getActivity(appContext.get, getUniqueId, notificationIntent, 0)
      val builder = new NotificationCompat.Builder(appContext.get)
      val title: String = appContext.get.getString(R.string.translating)
      builder
          .setContentTitle(title)
          .setTicker(title)
          .setContentIntent(contentIntent)
          .setSmallIcon(R.drawable.ic_launcher)
          .setAutoCancel(true)

      if (persistentServices.isHeadsUpEnable()) {
        builder
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
      }

      notifyManager.notify(NOTIFICATION_ID, builder.build())
    }

    override def failed(): Unit = {
      val notificationIntent: Intent = new Intent(appContext.get, classOf[MainActivity])
      val contentIntent: PendingIntent = PendingIntent.getActivity(appContext.get, getUniqueId, notificationIntent, 0)


      val builder = new NotificationCompat.Builder(appContext.get)
      val title: String = appContext.get.getString(R.string.failedTitle)
      val message: String = appContext.get.getString(R.string.failedMessage)
      val notification: Notification = builder
          .setContentTitle(title)
          .setContentText(message)
          .setTicker(title).setContentIntent(contentIntent)
          .setSmallIcon(R.drawable.ic_launcher)
          .setAutoCancel(true)
          .build

      notifyManager.notify(NOTIFICATION_ID, notification)
    }

    def getUniqueId: Int = {
      return (System.currentTimeMillis & 0xfffffff).toInt
    }

  }

}
