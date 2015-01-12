package com.fortysevendeg.translatebubble.modules.notifications

import com.fortysevendeg.translatebubble.service.Service

trait NotificationsServices {
  def showTextTranslated: Service[ShowTextTranslatedRequest, ShowTextTranslatedResponse]
  def failed(): Unit
}

trait NotificationsServicesComponent {
  val notificationsServices: NotificationsServices
}
