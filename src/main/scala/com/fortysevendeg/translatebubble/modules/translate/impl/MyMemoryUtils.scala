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

import java.net.URLEncoder

import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.commons.ContextWrapperProvider
import com.fortysevendeg.translatebubble.utils.LanguageType._
import com.fortysevendeg.translatebubble.utils.LanguageTypeTransformer

trait MyMemoryUtils {

  self : ContextWrapperProvider =>

  def getTranslateServiceUrl(text: String, from: LanguageType, to: LanguageType) =
    contextProvider.application.getString(R.string.translateServiceUrl,
      URLEncoder.encode(text, "UTF-8"),
      URLEncoder.encode("%s|%s".format(LanguageTypeTransformer.toMyMemory(from),
        LanguageTypeTransformer.toMyMemory(to)), "UTF-8"))

}
