package netcaty

import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}
import netcaty.http_https.client.Client
import netcaty.http_https.server.Server

object HttpHttps {
  type RequestHandler  = (FullHttpRequest, FullHttpResponse) => Unit
  type ResponseHandler = FullHttpResponse => Unit

  val MAX_CONTENT_LENGTH = 16 * 1024 * 1024
}

class HttpHttps(s: Boolean) {
  import HttpHttps._

  def respondOne(port: Int, handler: RequestHandler): Server = {
    val server = new Server(s, port, handler)
    server.start(true)
    server
  }

  def respond(port: Int, handler: RequestHandler): Server = {
    val server = new Server(s, port, handler)
    server.start(false)
    server
  }

  def request(host: String, port: Int, req: FullHttpRequest): FullHttpResponse = {
    val client = new Client(s, host, port, req)
    client.request()
  }

  def request(host: String, port: Int, req: FullHttpRequest, handler: ResponseHandler) {
    val client = new Client(s, host, port, req)
    client.request(handler)
  }
}

object http  extends HttpHttps(false)
object https extends HttpHttps(true)
