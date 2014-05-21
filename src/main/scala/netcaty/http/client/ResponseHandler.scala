package netcaty.http.client

import io.netty.channel.{
  ChannelHandlerContext, SimpleChannelInboundHandler,
  ChannelFuture, ChannelFutureListener
}
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.util.concurrent.Promise

class ResponseHandler(resPromise: Promise[FullHttpResponse]) extends SimpleChannelInboundHandler[FullHttpResponse] {
  override def channelRead0(ctx: ChannelHandlerContext, res: FullHttpResponse) {
    resPromise.setSuccess(res.retain())

    val ch = ctx.channel
    if (ch.isOpen) ch.close()
  }
}
