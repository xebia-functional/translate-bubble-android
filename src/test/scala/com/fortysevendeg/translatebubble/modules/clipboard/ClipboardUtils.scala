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

import android.content.{ClipData, ClipboardManager, Context}
import com.fortysevendeg.translatebubble.commons.ContextWrapperProvider
import com.fortysevendeg.translatebubble.modules.TestConfig
import com.fortysevendeg.translatebubble.modules.clipboard.impl.{ClipDataBuilder, ClipboardServicesComponentImpl}
import macroid.ContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait ClipboardMocks
    extends BaseClipboardMocks {

  val mockClipData = mock[ClipData]
  val mockClipItem = mock[ClipData.Item]

  mockClipData.getItemAt(0) returns mockClipItem
  clipboardManager.getPrimaryClip returns mockClipData
}

trait BaseClipboardMocks
    extends Mockito
    with ContextWrapperProvider
    with ClipboardServicesComponentImpl
    with TestConfig
    with Scope {
  implicit val contextProvider: ContextWrapper = mock[ContextWrapper]

  override lazy val clipDataBuilder = mock[ClipDataBuilder]

  val clipboardManager = mock[ClipboardManager]
  mockContext.getSystemService(Context.CLIPBOARD_SERVICE) returns clipboardManager
  contextProvider.application returns mockContext
}