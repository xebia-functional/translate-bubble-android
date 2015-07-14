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

import macroid.ContextWrapper
import com.fortysevendeg.macroid.extras.ResourcesExtras._

sealed trait TranslateUiType

object TranslateUiType {

  def unapply(t: TranslateUiType): String = t.toString

  val types: List[String] = List(Bubble.toString, Notification.toString)

  def toSortedTuples()(implicit contextWrapper: ContextWrapper): List[(String, String)] = (types zip resourceNames).sortBy(_._2)

  private def resourceNames(implicit contextWrapper: ContextWrapper): List[String] =
    types map {
      v =>
        val id = resGetIdentifier(v, "string")
        id map resGetString getOrElse v
    }

}

case object Notification extends TranslateUiType

case object Bubble extends TranslateUiType

