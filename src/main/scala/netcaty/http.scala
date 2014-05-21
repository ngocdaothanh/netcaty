package netcaty

import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}
import netcaty.http.server.Server

package object http {
  type RequestHandler  = (FullHttpRequest, FullHttpResponse) => Unit
  type ResponseHandler = FullHttpResponse => Unit

  def respondOne(port: Int, handler: RequestHandler) {
    val server = new Server(port, handler)
    server.start(true)
  }

  def respond(port: Int, handler: RequestHandler): Server = {
    val server = new Server(port, handler)
    server.start(false)
    server
  }

  def request(host: String, port: Int, req: FullHttpRequest) {

  }

  def request(host: String, port: Int, req: FullHttpRequest, handler: ResponseHandler) {

  }
}
