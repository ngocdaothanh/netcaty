package netcaty.tcp.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.FixedLengthFrameDecoder
import netcaty.{tcp, Ssl}

class PipelineInitializer(
  ssl: Boolean, server: Server, requestLength: Int, handler: tcp.RequestHandler, stopAfterOneResponse: Boolean
) extends ChannelInitializer[SocketChannel] {
  def initChannel(ch: SocketChannel) {
    val p = ch.pipeline

    if (ssl) p.addLast(Ssl.serverContext.newHandler(ch.alloc))

    p.addLast(
      new FixedLengthFrameDecoder(requestLength),
      new RequestHandler(server, handler, stopAfterOneResponse)
    )
  }
}
