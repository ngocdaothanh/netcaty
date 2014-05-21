package netcaty.http.client

import io.netty.channel.{Channel, ChannelInitializer}
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{FullHttpResponse, HttpContentDecompressor, HttpObjectAggregator, HttpRequestEncoder, HttpResponseDecoder}
import io.netty.util.concurrent.Promise

class PipelineInitializer(resPromise: Promise[FullHttpResponse]) extends ChannelInitializer[SocketChannel] {
  def initChannel(ch: SocketChannel) {
    ch.pipeline.addLast(
      // Outbound
      new HttpRequestEncoder,

      // Inbound
      new HttpResponseDecoder,
      new HttpContentDecompressor,
      new HttpObjectAggregator(netcaty.http.MAX_CONTENT_LENGTH),  // Handle chunks
      new ResponseHandler(resPromise)
    )
  }
}
