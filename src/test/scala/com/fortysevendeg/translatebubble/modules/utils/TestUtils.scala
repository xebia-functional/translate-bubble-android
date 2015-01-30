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

package com.fortysevendeg.translatebubble.modules.utils

import android.content.{ClipData, ClipboardManager, Context}
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.TestConfig
import com.fortysevendeg.translatebubble.modules.clipboard.impl.{ClipDataBuilder, ClipboardServicesComponentImpl}
import macroid.AppContext
import org.specs2.matcher.{ExceptionMatchers, MustMatchers, ThrownExpectations}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}



object AsyncUtils {

  implicit class RichAsyncResponseMatcher[T](futureResult: Future[T])
      extends ThrownExpectations
      with ExceptionMatchers
      with MustMatchers {

    def *===[U](expected: => U) = Await.result(futureResult, Duration.Inf) === expected

  }

}

