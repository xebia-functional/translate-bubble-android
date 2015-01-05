package com.fortysevendeg.translatebubble.modules.notifications.impl

import android.app.{Notification, NotificationManager, PendingIntent}
import android.content.{Context, Intent}
import android.support.v4.app.NotificationCompat
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.notifications._
import com.fortysevendeg.translatebubble.modules.persistent.PersistentServicesComponent
import com.fortysevendeg.translatebubble.service.Service
import com.fortysevendeg.translatebubble.ui.preferences.MainActivity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait NotificationsServicesComponentImpl
    extends NotificationsServicesComponent {

  self : PersistentServicesComponent with AppContextProvider =>

  lazy val notificationsServices = new NotificationsServicesImpl

  class NotificationsServicesImpl
      extends NotificationsServices {

    private val NOTIFICATION_ID: Int = 1100

    val notifyManager = appContextProvider.get.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

    override def showTextTranslated: Service[ShowTextTranslatedRequest, ShowTextTranslatedResponse] = request =>
        Future {
          val notificationIntent: Intent = new Intent(appContextProvider.get, classOf[MainActivity])
          val contentIntent: PendingIntent = PendingIntent.getActivity(appContextProvider.get, getUniqueId, notificationIntent, 0)

          val builder = new NotificationCompat.Builder(appContextProvider.get)
          val title = appContextProvider.get.getString(R.string.translatedTitle, request.original)
          builder
              .setContentTitle(title)
              .setContentText(request.translated)
              .setTicker(title)
              .setContentIntent(contentIntent)
              .setSmallIcon(R.drawable.icon_app)
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

    override def translating(): Unit = {
      val notificationIntent: Intent = new Intent(appContextProvider.get, classOf[MainActivity])
      val contentIntent: PendingIntent = PendingIntent.getActivity(appContextProvider.get, getUniqueId, notificationIntent, 0)
      val builder = new NotificationCompat.Builder(appContextProvider.get)
      // TODO Simplify this using MacroidExtras when it's available
      val title = appContextProvider.get.getString(R.string.translating)
      builder
          .setContentTitle(title)
          .setTicker(title)
          .setContentIntent(contentIntent)
          .setSmallIcon(R.drawable.icon_app)
          .setAutoCancel(true)

      if (persistentServices.isHeadsUpEnable()) {
        builder
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
      }

      notifyManager.notify(NOTIFICATION_ID, builder.build())
    }

    override def failed(): Unit = {
      val notificationIntent: Intent = new Intent(appContextProvider.get, classOf[MainActivity])
      val contentIntent: PendingIntent = PendingIntent.getActivity(appContextProvider.get, getUniqueId, notificationIntent, 0)


      val builder = new NotificationCompat.Builder(appContextProvider.get)
      val title = appContextProvider.get.getString(R.string.failedTitle)
      val message = appContextProvider.get.getString(R.string.failedMessage)
      val notification: Notification = builder
          .setContentTitle(title)
          .setContentText(message)
          .setTicker(title).setContentIntent(contentIntent)
          .setSmallIcon(R.drawable.icon_app)
          .setAutoCancel(true)
          .build

      notifyManager.notify(NOTIFICATION_ID, notification)
    }

    def getUniqueId: Int = (System.currentTimeMillis & 0xfffffff).toInt

  }

}
