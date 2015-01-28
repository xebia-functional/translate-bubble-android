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

package com.fortysevendeg.translatebubble.modules.analytics.impl

import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.translatebubble.R
import com.fortysevendeg.translatebubble.modules.analytics.{AnalyticsServices, AnalyticsServicesComponent}
import com.google.android.gms.analytics.{HitBuilders, GoogleAnalytics}

trait AnalyticsServicesComponentImpl
    extends AnalyticsServicesComponent {

  self: AppContextProvider =>

  lazy val analyticsServices = new AnalyticsServicesImpl

  class AnalyticsServicesImpl
      extends AnalyticsServices {

    lazy val tracker = GoogleAnalytics
        .getInstance(appContextProvider.get)
        .newTracker(R.xml.app_tracker)


    def send(screenName: String,
        category: Option[String] = None,
        action: Option[String] = None,
        label: Option[String] = None): Unit = {
      tracker.setScreenName(screenName)
      val event = new HitBuilders.EventBuilder()
      category map event.setCategory
      action map event.setAction
      label map event.setLabel
      tracker.send(event.build())
    }

  }

}
