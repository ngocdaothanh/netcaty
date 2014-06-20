package netcaty

import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}
import netcaty.http.client.Client
import netcaty.http.server.Server

class Http(https: Boolean) {
  def respondOne(port: Int, handler: http.RequestHandler): Server = {
    val server = new Server(https, port, handler)
    server.start(true)
    server
  }

  def respond(port: Int, handler: http.RequestHandler): Server = {
    val server = new Server(https, port, handler)
    server.start(false)
    server
  }

  def request(host: String, port: Int, req: FullHttpRequest): FullHttpResponse = {
    val client = new Client(https, host, port, req)
    client.request()
  }

  def request(host: String, port: Int, req: FullHttpRequest, handler: http.ResponseHandler) {
    val client = new Client(https, host, port, req)
    client.request(handler)
  }
}

object Http  extends Http(false)
object Https extends Http(true)
