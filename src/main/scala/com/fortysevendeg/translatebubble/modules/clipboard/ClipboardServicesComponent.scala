package com.fortysevendeg.translatebubble.modules.clipboard

import android.content.ClipboardManager
import com.fortysevendeg.translatebubble.service._
import macroid.AppContext

trait ClipboardServices {
  def init(listener: ClipboardManager.OnPrimaryClipChangedListener)
  def destroy()
  def reset()
  def getText: Service[GetTextClipboardRequest, GetTextClipboardResponse]
  def copyToClipboard: Service[CopyToClipboardRequest, CopyToClipboardResponse]
}

trait ClipboardServicesComponent {
  def clipboardServices(implicit appContext: AppContext): ClipboardServices
}
