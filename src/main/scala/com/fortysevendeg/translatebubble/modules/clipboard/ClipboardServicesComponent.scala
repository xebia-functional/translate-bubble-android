package com.fortysevendeg.translatebubble.modules.clipboard

import android.content.ClipboardManager
import com.fortysevendeg.translatebubble.service._
import macroid.AppContext

trait ClipboardServices {
  def init(listener: ClipboardManager.OnPrimaryClipChangedListener): Unit
  def destroy(): Unit
  def reset(): Unit
  def getText: Service[GetTextClipboardRequest, GetTextClipboardResponse]
  def copyToClipboard: Service[CopyToClipboardRequest, CopyToClipboardResponse]
}

trait ClipboardServicesComponent {
  val clipboardServices: ClipboardServices
}
