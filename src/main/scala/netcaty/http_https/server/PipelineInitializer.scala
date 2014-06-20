package netcaty.http_https.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpRequestDecoder, HttpObjectAggregator, HttpResponseEncoder}
import io.netty.handler.stream.ChunkedWriteHandler

import netcaty.{HttpHttps, Ssl}

class PipelineInitializer(https: Boolean, server: Server, handler: HttpHttps.RequestHandler, stopAfterOneResponse: Boolean) extends ChannelInitializer[SocketChannel] {
  def initChannel(ch: SocketChannel) {
    val p = ch.pipeline

    if (https) p.addLast(Ssl.serverContext.newHandler(ch.alloc))

    // HttpObjectAggregator automatically sends "Continue" response for
    // "Expect 100 Continue" request.
    //
    // But: http://netty.io/4.0/api/io/netty/handler/codec/http/HttpObjectAggregator.html
    // "Be aware that you need to have the HttpResponseEncoder or HttpRequestEncoder
    // before the HttpObjectAggregator in the ChannelPipeline."
    p.addLast(
      // Outbound
      new HttpResponseEncoder,

      // Inbound
      new HttpRequestDecoder,
      new HttpObjectAggregator(HttpHttps.MAX_CONTENT_LENGTH),  // Handle chunks
      new RequestHandler(server, handler, stopAfterOneResponse)
    )
  }
}
