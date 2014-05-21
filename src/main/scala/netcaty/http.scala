package netcaty

import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}

import netcaty.http.client.Client
import netcaty.http.server.Server

package object http {
  type RequestHandler  = (FullHttpRequest, FullHttpResponse) => Unit
  type ResponseHandler = FullHttpResponse => Unit

  val MAX_CONTENT_LENGTH = 16 * 1024 * 1024

  def respondOne(port: Int, handler: RequestHandler) {
    val server = new Server(port, handler)
    server.start(true)
  }

  def respond(port: Int, handler: RequestHandler): Server = {
    val server = new Server(port, handler)
    server.start(false)
    server
  }

  def request(host: String, port: Int, req: FullHttpRequest): FullHttpResponse = {
    val client = new Client(host, port, req)
    client.request()
  }

  def request(host: String, port: Int, req: FullHttpRequest, handler: ResponseHandler) {

  }
}
