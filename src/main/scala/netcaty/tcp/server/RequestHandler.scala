package netcaty.tcp.server

import io.netty.buffer.ByteBuf
import io.netty.channel.{
  ChannelHandlerContext, SimpleChannelInboundHandler,
  ChannelFuture, ChannelFutureListener
}
import netcaty.tcp

class RequestHandler(server: Server, handler: tcp.RequestHandler, stopAfterOneResponse: Boolean)
  extends SimpleChannelInboundHandler[ByteBuf]
{
  override def channelRead0(ctx: ChannelHandlerContext, req: ByteBuf) {
    val bytes = new Array[Byte](req.readableBytes)
    req.readBytes(bytes)

    val res = handler(bytes)

    // resBuf will be automatically released
    val ch     = ctx.channel
    val resBuf = ch.alloc.buffer(res.length).writeBytes(res)
    val future = ch.writeAndFlush(resBuf)
    if (stopAfterOneResponse)
      future.addListener(new ChannelFutureListener {
        override def operationComplete(future: ChannelFuture) {
          server.stop()
        }
      })
    else
      future.addListener(ChannelFutureListener.CLOSE)
  }
}
