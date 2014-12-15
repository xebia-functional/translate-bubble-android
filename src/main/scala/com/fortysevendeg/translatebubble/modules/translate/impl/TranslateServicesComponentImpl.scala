package com.fortysevendeg.translatebubble.modules.translate.impl

import java.io._
import java.net.URLEncoder

import com.fortysevendeg.translatebubble.modules.persistent.impl.PersistentServicesComponentImpl
import com.fortysevendeg.translatebubble.modules.translate.{TranslateRequest, TranslateResponse, TranslateServices, TranslateServicesComponent}
import com.fortysevendeg.translatebubble.service.Service
import com.fortysevendeg.translatebubble.utils.TypeLanguageTransformer
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.{HttpEntity, HttpResponse}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TranslateServicesComponentImpl
    extends TranslateServicesComponent {

  val translateServices = new TranslateServicesImpl

  class TranslateServicesImpl
      extends TranslateServices {

    override def translate: Service[TranslateRequest, TranslateResponse] = {
      request =>
        Future {
          request.text.map {
            text =>
              var urlMaybe: Option[String] = None
              try {
                urlMaybe = Some("http://api.mymemory.translated.net/get?q="
                    + URLEncoder.encode(text, "UTF-8")
                    + "&langpair=" + URLEncoder.encode(TypeLanguageTransformer.toMyMemory(request.from)
                    + "|" + TypeLanguageTransformer.toMyMemory(request.to), "UTF-8"))
              }
              catch {
                case e: UnsupportedEncodingException => e.printStackTrace()
              }

              urlMaybe.map(
                f = url => {
                  try {
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
                    val json = parse(sb.toString())
                    val translatedText = (json \ "responseData" \ "translatedText").extract[String]
                    TranslateResponse(Some(translatedText))
                  } catch {
                    case e: UnsupportedEncodingException => {
                      e.printStackTrace()
                      TranslateResponse(None)
                    }
                    case e: ClientProtocolException => {
                      e.printStackTrace()
                      TranslateResponse(None)
                    }
                    case e: IOException => {
                      e.printStackTrace()
                      TranslateResponse(None)
                    }
                    case e: Throwable => {
                      e.printStackTrace()
                      TranslateResponse(None)
                    }
                  }
                }
              ).getOrElse(TranslateResponse(None))
          }.getOrElse(TranslateResponse(None))
        }
    }
  }

}