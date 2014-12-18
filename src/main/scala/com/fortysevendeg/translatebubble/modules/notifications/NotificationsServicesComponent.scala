package com.fortysevendeg.translatebubble.modules.notifications

import macroid.AppContext

import com.fortysevendeg.translatebubble.service.Service

trait NotificationsServices {
  def showTextTranslated: Service[ShowTextTranslatedRequest, ShowTextTranslatedResponse]
  def translating()
  def failed()
}

trait NotificationsServicesComponent {
  def notificationsServices: NotificationsServices
}
