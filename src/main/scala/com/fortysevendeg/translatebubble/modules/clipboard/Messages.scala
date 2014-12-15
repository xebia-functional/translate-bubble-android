package com.fortysevendeg.translatebubble.modules.clipboard

case class GetTextClipboardRequest()

case class GetTextClipboardResponse(text: Option[String])

case class CopyToClipboardRequest(text: String)

case class CopyToClipboardResponse()