package com.fortysevendeg.translatebubble.modules.translate

import com.fortysevendeg.translatebubble.service._

trait TranslateServices {
  def translate: Service[TranslateRequest, TranslateResponse]
}

trait TranslateServicesComponent {
  def translateServices: TranslateServices
}
