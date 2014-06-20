package netcaty.http.client

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{FullHttpResponse, HttpContentDecompressor, HttpObjectAggregator, HttpRequestEncoder, HttpResponseDecoder}
import io.netty.util.concurrent.Promise
import netcaty.{http, Ssl}

class PipelineInitializer(https: Boolean, resPromise_or_handler: Either[Promise[FullHttpResponse], http.ResponseHandler])
  extends ChannelInitializer[SocketChannel]
{
  def initChannel(ch: SocketChannel) {
    val p = ch.pipeline

    if (https) p.addLast(Ssl.clientContext.newHandler(ch.alloc))

    // http://netty.io/4.0/api/io/netty/handler/codec/http/HttpObjectAggregator.html
    // "Be aware that you need to have the HttpResponseEncoder or HttpRequestEncoder
    // before the HttpObjectAggregator in the ChannelPipeline."
    p.addLast(
      // Outbound
      new HttpRequestEncoder,

      // Inbound
      new HttpResponseDecoder,
      new HttpContentDecompressor,
      new HttpObjectAggregator(http.MAX_CONTENT_LENGTH),  // Handle chunks
      new ResponseHandler(resPromise_or_handler)
    )
  }
}
