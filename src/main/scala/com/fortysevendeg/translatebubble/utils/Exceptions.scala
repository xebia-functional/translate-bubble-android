package com.fortysevendeg.translatebubble.utils

package object Exceptions {

  case class ClipboardException(message: String) extends Exception

  case class TranslationException(message: String) extends Exception

  case class ProviderException(message: String) extends Exception
}
