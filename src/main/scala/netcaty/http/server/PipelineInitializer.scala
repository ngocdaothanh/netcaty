package netcaty.http.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpRequestDecoder, HttpObjectAggregator, HttpResponseEncoder}
import io.netty.handler.stream.ChunkedWriteHandler

class PipelineInitializer(server: Server, handler: netcaty.http.RequestHandler, stopAfterOneResponse: Boolean) extends ChannelInitializer[SocketChannel] {
  def initChannel(ch: SocketChannel) {
    ch.pipeline.addLast(
      // Inbound
      new HttpRequestDecoder,
      new HttpObjectAggregator(netcaty.http.MAX_CONTENT_LENGTH),  // Handle chunks
      new RequestHandler(server, handler, stopAfterOneResponse),

      // Outbound
      new HttpResponseEncoder
    )
  }
}
