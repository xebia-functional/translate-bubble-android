package com.fortysevendeg.translatebubble.modules

import com.fortysevendeg.translatebubble.modules.clipboard.{ClipboardServicesComponent, ClipboardServices}
import com.fortysevendeg.translatebubble.modules.notifications.{NotificationsServicesComponent, NotificationsServices}
import com.fortysevendeg.translatebubble.modules.persistent.{PersistentServicesComponent, PersistentServices}
import com.fortysevendeg.translatebubble.modules.translate.{TranslateServicesComponent, TranslateServices}

trait ComponentRegistry
    extends ClipboardServicesComponent
    with PersistentServicesComponent
    with TranslateServicesComponent
    with NotificationsServicesComponent
