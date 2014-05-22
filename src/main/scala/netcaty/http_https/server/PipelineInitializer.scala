package netcaty.http_https.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpRequestDecoder, HttpObjectAggregator, HttpResponseEncoder}
import io.netty.handler.stream.ChunkedWriteHandler

import netcaty.HttpHttps
import netcaty.ssl.Ssl

class PipelineInitializer(https: Boolean, server: Server, handler: HttpHttps.RequestHandler, stopAfterOneResponse: Boolean) extends ChannelInitializer[SocketChannel] {
  def initChannel(ch: SocketChannel) {
    val p = ch.pipeline

    if (https) p.addLast(Ssl.createServerHandler())

    p.addLast(
      // Inbound
      new HttpRequestDecoder,
      new HttpObjectAggregator(HttpHttps.MAX_CONTENT_LENGTH),  // Handle chunks
      new RequestHandler(server, handler, stopAfterOneResponse),

      // Outbound
      new HttpResponseEncoder
    )
  }
}
