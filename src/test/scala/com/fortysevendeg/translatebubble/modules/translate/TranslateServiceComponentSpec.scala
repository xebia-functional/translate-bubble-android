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

package com.fortysevendeg.translatebubble.modules.translate

import com.fortysevendeg.translatebubble.utils.LanguageType
import com.fortysevendeg.translatebubble.utils.LanguageType._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import com.fortysevendeg.translatebubble.modules.utils.AsyncUtils._

class TranslateServiceComponentSpec
    extends Specification
    with Mockito {

  "TranslateService component" should {

    "TranslateService should get text translated from json" in new BaseTranslateMocks {

      override def getJson(url: String): Option[String] = Some("{\"responseData\":{\"translatedText\":\"Hola mundo!\",\"match\":1},\"responseDetails\":\"\",\"responseStatus\":200,\"responderId\":\"236\",\"matches\":[{\"id\":\"464820185\",\"segment\":\"Hello World!\",\"translation\":\"Ciao mondo!\",\"quality\":\"74\",\"reference\":\"\",\"usage-count\":7,\"subject\":\"All\",\"created-by\":\"Matecat\",\"last-updated-by\":\"Matecat\",\"create-date\":\"2015-01-07 15:28:34\",\"last-update-date\":\"2015-01-07 15:28:34\",\"tm_properties\":\"\",\"match\":1},{\"id\":\"446770249\",\"segment\":\"Hello, World!\",\"translation\":\"\\u00a1Hola, mundo!\",\"quality\":\"80\",\"reference\":\"\",\"usage-count\":40,\"subject\":\"All\",\"created-by\":\"Matecat\",\"last-updated-by\":\"Matecat\",\"create-date\":\"2014-11-30 20:08:01\",\"last-update-date\":\"2014-11-30 20:08:01\",\"tm_properties\":\"\",\"match\":0.98},{\"id\":\"437420561\",\"segment\":\"Hello world\",\"translation\":\"Hola el mundo!\",\"quality\":\"74\",\"reference\":\"\",\"usage-count\":1,\"subject\":\"All\",\"created-by\":\"Matecat\",\"last-updated-by\":\"Matecat\",\"create-date\":\"2014-01-07 01:19:21\",\"last-update-date\":\"2014-01-07 01:19:21\",\"tm_properties\":null,\"match\":0.97}]}")

      override def getTranslateServiceUrl(text: String, from: LanguageType, to: LanguageType) = "http://fakeUrl"

      val text = Some("text")
      val from = LanguageType.ENGLISH
      val to = LanguageType.SPANISH

      translateServices.translate(TranslateRequest(text, from, to)) *=== TranslateResponse(Some("Hola mundo!"))

    }

    "TranslateService should get None when no json available" in new BaseTranslateMocks {

      override def getJson(url: String): Option[String] = None

      override def getTranslateServiceUrl(text: String, from: LanguageType, to: LanguageType) = "http://fakeUrl"

      val text = Some("text")
      val from = LanguageType.ENGLISH
      val to = LanguageType.SPANISH

      translateServices.translate(TranslateRequest(text, from, to)) *=== TranslateResponse(None)

    }

    "TranslateService should get None when json is invalid" in new BaseTranslateMocks {

      override def getJson(url: String): Option[String] = Some("{ invalidJson }")

      override def getTranslateServiceUrl(text: String, from: LanguageType, to: LanguageType) = "http://fakeUrl"

      val text = Some("text")
      val from = LanguageType.ENGLISH
      val to = LanguageType.SPANISH

      translateServices.translate(TranslateRequest(text, from, to)) *=== TranslateResponse(None)

    }

  }

}
