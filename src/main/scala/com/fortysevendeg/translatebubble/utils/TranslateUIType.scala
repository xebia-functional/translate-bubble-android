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

object TranslateUIType extends Enumeration {
  type TypeTranslateUI = Value
  val NOTIFICATION, BUBBLE = Value

  def toSortedTuples()(implicit appContext: AppContext) = (stringNames zip resourceNames).sortBy(_._2)

  val stringNames: List[String] = TranslateUIType.values.toList.map(_.toString)

  private def resourceNames(implicit appContext: AppContext): List[String] =
    TranslateUIType.values.toList.map {
      v =>
        val id = appContext.get.getResources.getIdentifier(v.toString, "string", appContext.get.getPackageName)
        if (id == 0) v.toString else appContext.get.getString(id)
    }


}

