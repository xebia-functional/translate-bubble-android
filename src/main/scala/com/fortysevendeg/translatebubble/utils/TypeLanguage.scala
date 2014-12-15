package com.fortysevendeg.translatebubble.utils

import com.fortysevendeg.translatebubble.utils.TypeLanguage.TypeLanguage
import macroid.AppContext

object TypeLanguage extends Enumeration {

  type TypeLanguage = Value

  val ARABIC, BULGARIAN, CATALAN, CHINESE_SIMPLIFIED, CHINESE_TRADITIONAL, CZECH, DANISH, DUTCH,
  ENGLISH, ESTONIAN, FINNISH, FRENCH, GERMAN, GREEK, HAITIAN_CREOLE, HEBREW, HINDI, HMONG_DAW, HUNGARIAN,
  INDONESIAN, ITALIAN, JAPANESE, KOREAN, LATVIAN, LITHUANIAN, MALAY, NORWEGIAN, PERSIAN, POLISH, PORTUGUESE,
  ROMANIAN, RUSSIAN, SLOVAK, SLOVENIAN, SPANISH, SWEDISH, THAI, TURKISH, UKRAINIAN, URDU, VIETNAMESE = Value


  def stringNames(): List[String] = {
    var result: List[String] = List.empty
    for (value <- values) {
      result = value.toString :: result
    }
    result.reverse
  }

  def resourceNames(implicit appContext: AppContext): List[String] = {
    var result: List[String] = List.empty
    for (value <- values) {
      val id = appContext.get.getResources.getIdentifier(value.toString, "string", appContext.get.getPackageName)
      result = (if (id == 0) value.toString else appContext.get.getString(id)) :: result
    }
    result.reverse
  }

}

object TypeLanguageTransformer {
  def toMyMemory(value: TypeLanguage): String = {
    if (value.equals(TypeLanguage.ARABIC)) {
      return "ar-SA"
    }
    else if (value.equals(TypeLanguage.BULGARIAN)) {
      return "bg-BG"
    }
    else if (value.equals(TypeLanguage.CATALAN)) {
      return "ca-ES"
    }
    else if (value.equals(TypeLanguage.CHINESE_SIMPLIFIED)) {
      return "zh-CN"
    }
    else if (value.equals(TypeLanguage.CHINESE_TRADITIONAL)) {
      return "zh-TW"
    }
    else if (value.equals(TypeLanguage.CZECH)) {
      return "cs"
    }
    else if (value.equals(TypeLanguage.DANISH)) {
      return "da-DK"
    }
    else if (value.equals(TypeLanguage.DUTCH)) {
      return "nl-AN"
    }
    else if (value.equals(TypeLanguage.ENGLISH)) {
      return "en-GB"
    }
    else if (value.equals(TypeLanguage.ESTONIAN)) {
      return "et"
    }
    else if (value.equals(TypeLanguage.FINNISH)) {
      return "fi-FI"
    }
    else if (value.equals(TypeLanguage.FRENCH)) {
      return "fr-FR"
    }
    else if (value.equals(TypeLanguage.GERMAN)) {
      return "de-DE"
    }
    else if (value.equals(TypeLanguage.GREEK)) {
      return "el-GR"
    }
    else if (value.equals(TypeLanguage.HAITIAN_CREOLE)) {
      return "ht"
    }
    else if (value.equals(TypeLanguage.HEBREW)) {
      return "he"
    }
    else if (value.equals(TypeLanguage.HINDI)) {
      return "hi-IN"
    }
    else if (value.equals(TypeLanguage.HMONG_DAW)) {
      return "mww"
    }
    else if (value.equals(TypeLanguage.HUNGARIAN)) {
      return "hu-HU"
    }
    else if (value.equals(TypeLanguage.INDONESIAN)) {
      return "id-ID"
    }
    else if (value.equals(TypeLanguage.ITALIAN)) {
      return "it-IT"
    }
    else if (value.equals(TypeLanguage.JAPANESE)) {
      return "ja-JA"
    }
    else if (value.equals(TypeLanguage.KOREAN)) {
      return "ko-KR"
    }
    else if (value.equals(TypeLanguage.LATVIAN)) {
      return "lv"
    }
    else if (value.equals(TypeLanguage.LITHUANIAN)) {
      return "lt-LT"
    }
    else if (value.equals(TypeLanguage.MALAY)) {
      return "ms-MY"
    }
    else if (value.equals(TypeLanguage.NORWEGIAN)) {
      return "no-NO"
    }
    else if (value.equals(TypeLanguage.PERSIAN)) {
      return "fa-IR"
    }
    else if (value.equals(TypeLanguage.POLISH)) {
      return "pl-PL"
    }
    else if (value.equals(TypeLanguage.PORTUGUESE)) {
      return "pt-PT"
    }
    else if (value.equals(TypeLanguage.ROMANIAN)) {
      return "ro-RO"
    }
    else if (value.equals(TypeLanguage.RUSSIAN)) {
      return "ru-RU"
    }
    else if (value.equals(TypeLanguage.SLOVAK)) {
      return "sk-SK"
    }
    else if (value.equals(TypeLanguage.SLOVENIAN)) {
      return "sl-SI"
    }
    else if (value.equals(TypeLanguage.SPANISH)) {
      return "es-ES"
    }
    else if (value.equals(TypeLanguage.SWEDISH)) {
      return "sv-SE"
    }
    else if (value.equals(TypeLanguage.THAI)) {
      return "th-TH"
    }
    else if (value.equals(TypeLanguage.TURKISH)) {
      return "tr-TR"
    }
    else if (value.equals(TypeLanguage.UKRAINIAN)) {
      return "uk-UA"
    }
    else if (value.equals(TypeLanguage.URDU)) {
      return "ur-PK"
    }
    else if (value.equals(TypeLanguage.VIETNAMESE)) {
      return "vi-VN"
    }
    return "Autodetect"
  }

//  def toBing: Language = {
//    if (value.equals(TypeLanguage.ARABIC)) {
//      return Language.ARABIC
//    }
//    else if (value.equals(TypeLanguage.BULGARIAN)) {
//      return Language.BULGARIAN
//    }
//    else if (value.equals(TypeLanguage.CATALAN)) {
//      return Language.CATALAN
//    }
//    else if (value.equals(TypeLanguage.CHINESE_SIMPLIFIED)) {
//      return Language.CHINESE_SIMPLIFIED
//    }
//    else if (value.equals(TypeLanguage.CHINESE_TRADITIONAL)) {
//      return Language.CHINESE_TRADITIONAL
//    }
//    else if (value.equals(TypeLanguage.CZECH)) {
//      return Language.CZECH
//    }
//    else if (value.equals(TypeLanguage.DANISH)) {
//      return Language.DANISH
//    }
//    else if (value.equals(TypeLanguage.DUTCH)) {
//      return Language.DUTCH
//    }
//    else if (value.equals(TypeLanguage.ENGLISH)) {
//      return Language.ENGLISH
//    }
//    else if (value.equals(TypeLanguage.ESTONIAN)) {
//      return Language.ESTONIAN
//    }
//    else if (value.equals(TypeLanguage.FINNISH)) {
//      return Language.FINNISH
//    }
//    else if (value.equals(TypeLanguage.FRENCH)) {
//      return Language.FRENCH
//    }
//    else if (value.equals(TypeLanguage.GERMAN)) {
//      return Language.GERMAN
//    }
//    else if (value.equals(TypeLanguage.GREEK)) {
//      return Language.GREEK
//    }
//    else if (value.equals(TypeLanguage.HAITIAN_CREOLE)) {
//      return Language.HAITIAN_CREOLE
//    }
//    else if (value.equals(TypeLanguage.HEBREW)) {
//      return Language.HEBREW
//    }
//    else if (value.equals(TypeLanguage.HINDI)) {
//      return Language.HINDI
//    }
//    else if (value.equals(TypeLanguage.HMONG_DAW)) {
//      return Language.HMONG_DAW
//    }
//    else if (value.equals(TypeLanguage.HUNGARIAN)) {
//      return Language.HUNGARIAN
//    }
//    else if (value.equals(TypeLanguage.INDONESIAN)) {
//      return Language.INDONESIAN
//    }
//    else if (value.equals(TypeLanguage.ITALIAN)) {
//      return Language.ITALIAN
//    }
//    else if (value.equals(TypeLanguage.JAPANESE)) {
//      return Language.JAPANESE
//    }
//    else if (value.equals(TypeLanguage.KOREAN)) {
//      return Language.KOREAN
//    }
//    else if (value.equals(TypeLanguage.LATVIAN)) {
//      return Language.LATVIAN
//    }
//    else if (value.equals(TypeLanguage.LITHUANIAN)) {
//      return Language.LITHUANIAN
//    }
//    else if (value.equals(TypeLanguage.MALAY)) {
//      return Language.MALAY
//    }
//    else if (value.equals(TypeLanguage.NORWEGIAN)) {
//      return Language.NORWEGIAN
//    }
//    else if (value.equals(TypeLanguage.PERSIAN)) {
//      return Language.PERSIAN
//    }
//    else if (value.equals(TypeLanguage.POLISH)) {
//      return Language.POLISH
//    }
//    else if (value.equals(TypeLanguage.PORTUGUESE)) {
//      return Language.PORTUGUESE
//    }
//    else if (value.equals(TypeLanguage.ROMANIAN)) {
//      return Language.ROMANIAN
//    }
//    else if (value.equals(TypeLanguage.RUSSIAN)) {
//      return Language.RUSSIAN
//    }
//    else if (value.equals(TypeLanguage.SLOVAK)) {
//      return Language.SLOVAK
//    }
//    else if (value.equals(TypeLanguage.SLOVENIAN)) {
//      return Language.SLOVENIAN
//    }
//    else if (value.equals(TypeLanguage.SPANISH)) {
//      return Language.SPANISH
//    }
//    else if (value.equals(TypeLanguage.SWEDISH)) {
//      return Language.SWEDISH
//    }
//    else if (value.equals(TypeLanguage.THAI)) {
//      return Language.THAI
//    }
//    else if (value.equals(TypeLanguage.TURKISH)) {
//      return Language.TURKISH
//    }
//    else if (value.equals(TypeLanguage.UKRAINIAN)) {
//      return Language.UKRAINIAN
//    }
//    else if (value.equals(TypeLanguage.URDU)) {
//      return Language.URDU
//    }
//    else if (value.equals(TypeLanguage.VIETNAMESE)) {
//      return Language.VIETNAMESE
//    }
//    return Language.AUTO_DETECT
//  }

}
