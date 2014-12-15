package com.fortysevendeg.translatebubble

import scala.concurrent.Future

package object service {

  type Service[Req, Res] = Req => Future[Res]

}
