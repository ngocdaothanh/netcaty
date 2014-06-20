package netcaty

import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}
import io.netty.handler.codec.http.HttpHeaders
import io.netty.util.CharsetUtil

import netcaty.http.client.Client
import netcaty.http.server.Server

class Http(https: Boolean) {
  //----------------------------------------------------------------------------
  // Basic utility methods

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

  /** Starts server at a random open port. */
  def respondOne(handler: http.RequestHandler): Int =
    respondOne(0, handler).getPort

  /** Starts server at a random open port. */
  def respond(handler: http.RequestHandler): Int =
    respond(0, handler).getPort

  def request(host: String, port: Int, req: FullHttpRequest): FullHttpResponse = {
    Client.request(https, host, port, req)
  }

  def request(host: String, port: Int, req: FullHttpRequest, handler: http.ResponseHandler) {
    Client.request(https, host, port, req, handler)
  }

  //----------------------------------------------------------------------------
  // Additional utility methods

  def respondContentOne(port: Int, contentType: String, content: String): Server = {
    val server = respondOne(port, { case (req, res) =>
      val bytes = content.getBytes(CharsetUtil.UTF_8)
      res.headers.set(HttpHeaders.Names.CONTENT_TYPE,   contentType)
      res.headers.set(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
      res.content.writeBytes(bytes)
    })
    server
  }

  def respondContentOne(contentType: String, content: String): Int =
    respondContentOne(0, contentType, content).getPort
}

object Http  extends Http(false)
object Https extends Http(true)
