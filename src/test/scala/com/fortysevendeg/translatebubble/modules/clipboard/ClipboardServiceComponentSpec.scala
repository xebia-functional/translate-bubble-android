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

import android.content.ClipData
import com.fortysevendeg.translatebubble.modules.utils.AsyncUtils._
import org.specs2.mock.Mockito
import org.specs2.mutable._

class ClipboardServiceComponentSpec
    extends Specification
    with Mockito {


  "ClipboardService component" should {

    "ClipboardService should get a text" in new ClipboardMocks {
      val text = "test"
      mockClipItem.getText returns text

      clipboardServices.getText(GetTextClipboardRequest()) *=== GetTextClipboardResponse(Some(text))

      there was one(mockClipItem).getText
    }

    "ClipboardService should get None if text is empty" in new ClipboardMocks {
      val empty = ""
      mockClipItem.getText returns empty

      clipboardServices.getText(GetTextClipboardRequest()) *=== GetTextClipboardResponse(None)

      there was one(mockClipItem).getText
    }

    "ClipboardService should copy to clipboard" in new BaseClipboardMocks {
      val text = "text"
      val request = CopyToClipboardRequest(text)
      val mockClipData = mock[ClipData]

      clipDataBuilder.newPlainText(text) returns mockClipData

      clipboardServices.copyToClipboard(request) *=== CopyToClipboardResponse()

      there was one(clipboardManager).setPrimaryClip(mockClipData)

    }

    "ClipboardService should validate text if the text value is not a number neither an url" in new ClipboardMocks {
      val text = "This is not a number"
      mockClipItem.getText returns text

      clipboardServices.isValidCall must beTrue
    }

    "ClipboardService should not validate text if the text value is a number" in new ClipboardMocks {
      val number = "47"
      mockClipItem.getText returns number

      clipboardServices.isValidCall must beFalse
    }

    "ClipboardService should not validate text if the text value is a URL starting with 'https'" in new ClipboardMocks {
      val url = "https://www.47deg.com"
      mockClipItem.getText returns url

      clipboardServices.isValidCall must beFalse
    }

    "ClipboardService should not validate text if the text value is a URL starting with 'http'" in new ClipboardMocks {
      val url = "http://www.47deg.com"
      mockClipItem.getText returns url

      clipboardServices.isValidCall must beFalse
    }

    "ClipboardService should not validate text if the text value is a URL starting with 'www'" in new ClipboardMocks {
      val url = "www.47deg.com"
      mockClipItem.getText returns url

      clipboardServices.isValidCall must beFalse
    }

    "ClipboardService should not validate text if the text value is a URL starting with the domain" in new ClipboardMocks {
      val url = "47deg.com"
      mockClipItem.getText returns url

      clipboardServices.isValidCall must beFalse
    }

  }

}
