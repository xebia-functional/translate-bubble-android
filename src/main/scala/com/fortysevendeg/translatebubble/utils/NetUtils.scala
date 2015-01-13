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

import java.io.{BufferedReader, InputStream, InputStreamReader}

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.{HttpEntity, HttpResponse}

import scala.util.{Failure, Success, Try}

object NetUtils {

  def getJson(url: String): Option[String] = {
    Try {
      val httpClient: DefaultHttpClient = new DefaultHttpClient
      val httpPost: HttpGet = new HttpGet(url)
      val httpResponse: HttpResponse = httpClient.execute(httpPost)
      val httpEntity: HttpEntity = httpResponse.getEntity
      val is: InputStream = httpEntity.getContent
      val reader: BufferedReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8)
      val sb: StringBuilder = new StringBuilder
      var line: String = null
      while ( {
        line = reader.readLine
        line
      } != null) {
        sb.append(line + "\n")
      }
      is.close()

      implicit val formats = org.json4s.DefaultFormats
      sb.toString()
    } match {
      case Success(response) => Some(response)
      case Failure(ex) => {
        ex.printStackTrace()
        None
      }
    }
  }

}
