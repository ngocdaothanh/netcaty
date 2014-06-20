package netcaty

import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}

package object http {
  type RequestHandler  = (FullHttpRequest, FullHttpResponse) => Unit
  type ResponseHandler = FullHttpResponse => Unit

  val MAX_CONTENT_LENGTH = 16 * 1024 * 1024
}
