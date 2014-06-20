package netcaty

import io.netty.util.CharsetUtil
import netcaty.tcp.client.Client
import netcaty.tcp.server.Server

class Tcp(ssl: Boolean) {
  //----------------------------------------------------------------------------
  // Basic utility methods

  def respondOne(port: Int, resquestLength: Int, handler: tcp.RequestHandler): Server = {
    val server = new Server(ssl, port, resquestLength, handler)
    server.start(true)
    server
  }

  def respond(port: Int, resquestLength: Int, handler: tcp.RequestHandler): Server = {
    val server = new Server(ssl, port, resquestLength, handler)
    server.start(false)
    server
  }

  /** Starts server at a random open port. */
  def respondOne(resquestLength: Int, handler: tcp.RequestHandler): Int =
    respondOne(0, resquestLength, handler).getPort

  /** Starts server at a random open port. */
  def respond(resquestLength: Int, handler: tcp.RequestHandler): Int =
    respond(0, resquestLength, handler).getPort

  def request(host: String, port: Int, req: Array[Byte], responseLength: Int): Array[Byte] = {
    Client.request(ssl, host, port, req, responseLength)
  }

  def request(host: String, port: Int, req: Array[Byte], responseLength: Int, handler: tcp.ResponseHandler) {
    Client.request(ssl, host, port, req, responseLength, handler)
  }
}

object Tcp  extends Tcp(false)
object Tcps extends Tcp(true)
