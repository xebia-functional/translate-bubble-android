package com.fortysevendeg.translatebubble.modules.notifications

import macroid.AppContext

import com.fortysevendeg.translatebubble.service.Service

trait NotificationsServices {
  def showTextTranslated: Service[ShowTextTranslatedRequest, ShowTextTranslatedResponse]
  def translating(): Unit
  def failed(): Unit
}

trait NotificationsServicesComponent {
  val notificationsServices: NotificationsServices
}
