/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortysevendeg.translatebubble.utils

import macroid.AppContext

object LanguageType extends Enumeration {

  type LanguageType = Value

  val ARABIC, BULGARIAN, CATALAN, CHINESE_SIMPLIFIED, CHINESE_TRADITIONAL, CZECH, DANISH, DUTCH,
  ENGLISH, ESTONIAN, FINNISH, FRENCH, GERMAN, GREEK, HAITIAN_CREOLE, HEBREW, HINDI, HMONG_DAW, HUNGARIAN,
  INDONESIAN, ITALIAN, JAPANESE, KOREAN, LATVIAN, LITHUANIAN, MALAY, NORWEGIAN, PERSIAN, POLISH, PORTUGUESE,
  ROMANIAN, RUSSIAN, SLOVAK, SLOVENIAN, SPANISH, SWEDISH, THAI, TURKISH, UKRAINIAN, URDU, VIETNAMESE = Value

  def toSortedTuples()(implicit appContext: AppContext) = (stringNames zip resourceNames).sortBy(_._2)

  val stringNames: List[String] = LanguageType.values.toList.map(_.toString)

  private def resourceNames(implicit appContext: AppContext): List[String] =
    LanguageType.values.toList.map {
      v =>
        val id = appContext.get.getResources.getIdentifier(v.toString, "string", appContext.get.getPackageName)
        if (id == 0) v.toString else appContext.get.getString(id)
    }
}

object LanguageTypeTransformer {

  import com.fortysevendeg.translatebubble.utils.LanguageType._

  def fromMyMemory(value: String): LanguageType = value match {
    case "ar-SA" => ARABIC
    case "bg-BG" => BULGARIAN
    case "ca-ES" => CATALAN
    case "zh-CN" => CHINESE_SIMPLIFIED
    case "zh-TW" => CHINESE_TRADITIONAL
    case "cs" => CZECH
    case "da-DK" => DANISH
    case "nl-AN" => DUTCH
    case "en-GB" => ENGLISH
    case "et" => ESTONIAN
    case "fi-FI" => FINNISH
    case "fr-FR" => FRENCH
    case "de-DE" => GERMAN
    case "el-GR" => GREEK
    case "ht" => HAITIAN_CREOLE
    case "he" => HEBREW
    case "hi-IN" => HINDI
    case "mww" => HMONG_DAW
    case "hu-HU" => HUNGARIAN
    case "id-ID" => INDONESIAN
    case "it-IT" => ITALIAN
    case "ja-JA" => JAPANESE
    case "ko-KR" => KOREAN
    case "lv" => LATVIAN
    case "lt-LT" => LITHUANIAN
    case "ms-MY" => MALAY
    case "no-NO" => NORWEGIAN
    case "fa-IR" => PERSIAN
    case "pl-PL" => POLISH
    case "pt-PT" => PORTUGUESE
    case "ro-RO" => ROMANIAN
    case "ru-RU" => RUSSIAN
    case "sk-SK" => SLOVAK
    case "sl-SI" => SLOVENIAN
    case "es-ES" => SPANISH
    case "sv-SE" => SWEDISH
    case "th-TH" => THAI
    case "tr-TR" => TURKISH
    case "uk-UA" => UKRAINIAN
    case "ur-PK" => URDU
    case "vi-VN" => VIETNAMESE
  }

  def toMyMemory(value: LanguageType): String = value match {
    case ARABIC => "ar-SA"
    case BULGARIAN => "bg-BG"
    case CATALAN => "ca-ES"
    case CHINESE_SIMPLIFIED => "zh-CN"
    case CHINESE_TRADITIONAL => "zh-TW"
    case CZECH => "cs"
    case DANISH => "da-DK"
    case DUTCH => "nl-AN"
    case ENGLISH => "en-GB"
    case ESTONIAN => "et"
    case FINNISH => "fi-FI"
    case FRENCH => "fr-FR"
    case GERMAN => "de-DE"
    case GREEK => "el-GR"
    case HAITIAN_CREOLE => "ht"
    case HEBREW => "he"
    case HINDI => "hi-IN"
    case HMONG_DAW => "mww"
    case HUNGARIAN => "hu-HU"
    case INDONESIAN => "id-ID"
    case ITALIAN => "it-IT"
    case JAPANESE => "ja-JA"
    case KOREAN => "ko-KR"
    case LATVIAN => "lv"
    case LITHUANIAN => "lt-LT"
    case MALAY => "ms-MY"
    case NORWEGIAN => "no-NO"
    case PERSIAN => "fa-IR"
    case POLISH => "pl-PL"
    case PORTUGUESE => "pt-PT"
    case ROMANIAN => "ro-RO"
    case RUSSIAN => "ru-RU"
    case SLOVAK => "sk-SK"
    case SLOVENIAN => "sl-SI"
    case SPANISH => "es-ES"
    case SWEDISH => "sv-SE"
    case THAI => "th-TH"
    case TURKISH => "tr-TR"
    case UKRAINIAN => "uk-UA"
    case URDU => "ur-PK"
    case VIETNAMESE => "vi-VN"
    case _ => "Autodetect"
  }

}
