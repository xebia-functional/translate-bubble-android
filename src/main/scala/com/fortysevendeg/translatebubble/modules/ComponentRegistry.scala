package com.fortysevendeg.translatebubble.modules

import com.fortysevendeg.translatebubble.modules.clipboard.ClipboardServices
import com.fortysevendeg.translatebubble.modules.notifications.NotificationsServices
import com.fortysevendeg.translatebubble.modules.persistent.PersistentServices
import com.fortysevendeg.translatebubble.modules.translate.TranslateServices

trait ComponentRegistry
    extends ClipboardServices
    with PersistentServices
    with TranslateServices
    with NotificationsServices
