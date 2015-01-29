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

package com.fortysevendeg.translatebubble.modules.clipboard

import android.content.ClipData.Item
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.modules.clipboard.impl.{ClipDataBuilder, ClipboardServicesComponentImpl}
import com.fortysevendeg.translatebubble.modules.{TestConfig, BaseTestSupport}
import macroid.AppContext
import org.specs2.mock.Mockito
import org.specs2.mutable._
import android.content.{Context, ClipData, ClipboardManager}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ClipboardServiceComponentSpec
    extends Specification
    with ClipboardSupportTestSupport
    with AppContextProvider
    with ClipboardServicesComponentImpl {

  override lazy val clipDataBuilder = mock[ClipDataBuilder]

  "ClipboardService component" should {

    "ClipboardService should get a text" in {
      val out = "test"
      mockClipItem.getText returns (out)
      val response = Await.result(clipboardServices.getText(GetTextClipboardRequest()), Duration.Inf)
      there was one(mockClipItem).getText()
      response.text shouldEqual Some(out)
    }

    "ClipboardService should copy to clipboard" in {
      val text = "text"
      val request = CopyToClipboardRequest(text)

      clipDataBuilder.newPlainText(text) returns (mock[ClipData])

      val response = Await.result(clipboardServices.copyToClipboard(request), Duration.Inf)
      response must haveClass[CopyToClipboardResponse]
    }

  }

}

trait ClipboardSupportTestSupport
    extends BaseTestSupport
    with TestConfig
    with Mockito {

  val clipboardManager = mock[ClipboardManager]

  implicit val appContextProvider: AppContext = mock[AppContext]
  mockContext.getSystemService(Context.CLIPBOARD_SERVICE) returns (clipboardManager)
  appContextProvider.get returns (mockContext)

  val mockClipData = mock[ClipData]
  val mockClipItem = mock[ClipData.Item]

  mockClipData.getItemAt(0) returns (mockClipItem)
  clipboardManager.getPrimaryClip returns (mockClipData)

}