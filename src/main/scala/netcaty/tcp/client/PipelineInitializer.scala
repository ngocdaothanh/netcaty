package netcaty.tcp.client

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.FixedLengthFrameDecoder
import io.netty.util.concurrent.Promise

import netcaty.{tcp, Ssl}

class PipelineInitializer(
  https: Boolean, responseLength: Int, resPromise_or_handler: Either[Promise[Array[Byte]], tcp.ResponseHandler]
) extends ChannelInitializer[SocketChannel]
{
  def initChannel(ch: SocketChannel) {
    val p = ch.pipeline

    if (https) p.addLast(Ssl.clientContext.newHandler(ch.alloc))

    p.addLast(
      new FixedLengthFrameDecoder(responseLength),
      new ResponseHandler(resPromise_or_handler)
    )
  }
}
