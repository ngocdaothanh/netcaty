package netcaty.http.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpRequestDecoder, HttpObjectAggregator, HttpResponseEncoder}
import io.netty.handler.stream.ChunkedWriteHandler

class PipelineInitializer(server: Server, handler: netcaty.http.RequestHandler, stopAfterOneResponse: Boolean) extends ChannelInitializer[SocketChannel] {
  def initChannel(ch: SocketChannel) {
    val p = ch.pipeline

    p.addLast(
      // Inbound
      new HttpRequestDecoder,
      new HttpObjectAggregator(16 * 1024 * 1024),  // Handle chunks
      new RequestHandler(server, handler, stopAfterOneResponse),

      // Outbound
      new HttpResponseEncoder
    )
  }
}
