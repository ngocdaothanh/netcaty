package netcaty.http_https.client

import io.netty.channel.{Channel, ChannelInitializer}
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{FullHttpResponse, HttpContentDecompressor, HttpObjectAggregator, HttpRequestEncoder, HttpResponseDecoder}
import io.netty.util.concurrent.Promise

import netcaty.HttpHttps
import netcaty.ssl.Ssl

class PipelineInitializer(https: Boolean, resPromise_or_handler: Either[Promise[FullHttpResponse], HttpHttps.ResponseHandler])
  extends ChannelInitializer[SocketChannel]
{
  def initChannel(ch: SocketChannel) {
    val p = ch.pipeline

    if (https) p.addLast(Ssl.createClientHandler())

    p.addLast(
      // Outbound
      new HttpRequestEncoder,

      // Inbound
      new HttpResponseDecoder,
      new HttpContentDecompressor,
      new HttpObjectAggregator(HttpHttps.MAX_CONTENT_LENGTH),  // Handle chunks
      new ResponseHandler(resPromise_or_handler)
    )
  }
}
