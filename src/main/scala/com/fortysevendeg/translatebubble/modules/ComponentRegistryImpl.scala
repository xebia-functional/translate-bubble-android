package com.fortysevendeg.translatebubble.modules

import com.fortysevendeg.translatebubble.modules.clipboard.impl.ClipboardServicesComponentImpl
import com.fortysevendeg.translatebubble.modules.notifications.impl.NotificationsServicesComponentImpl
import com.fortysevendeg.translatebubble.modules.persistent.impl.PersistentServicesComponentImpl
import com.fortysevendeg.translatebubble.modules.translate.impl.TranslateServicesComponentImpl

trait ComponentRegistryImpl
    extends ComponentRegistry
    with ClipboardServicesComponentImpl
    with PersistentServicesComponentImpl
    with TranslateServicesComponentImpl
    with NotificationsServicesComponentImpl
