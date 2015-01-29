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

package com.fortysevendeg.translatebubble.modules.translate.impl

import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.translate.{TranslateRequest, TranslateResponse, TranslateServices, TranslateServicesComponent}
import com.fortysevendeg.translatebubble.service.Service
import com.fortysevendeg.translatebubble.utils.NetUtils
import org.apache.commons.lang3.StringEscapeUtils
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.language.postfixOps

trait TranslateServicesComponentImpl
    extends TranslateServicesComponent
    with NetUtils
    with MyMemoryUtils {

  self: AppContextProvider =>

  lazy val translateServices = new TranslateServicesImpl

  class TranslateServicesImpl
      extends TranslateServices {

    override def translate: Service[TranslateRequest, TranslateResponse] = request =>
      Future {
        request.text.map {
          text =>
            getJson(getTranslateServiceUrl(text, request.from, request.to)).map {
              jsonStr =>
                Try {
                  implicit val formats = org.json4s.DefaultFormats
                  val json = parse(jsonStr)
                  val translatedText = (json \ "responseData" \ "translatedText").extract[String]
                  StringEscapeUtils.unescapeHtml4(translatedText)
                } match {
                  case Success(response) => Some(response)
                  case Failure(ex) => None
                }
            }
        } flatten match {
          case Some(translatedText) => TranslateResponse(translatedText)
          case _ => TranslateResponse(None)
        }
      }
  }

}
