package netcaty.http.server

import io.netty.channel.{
  ChannelHandlerContext, SimpleChannelInboundHandler,
  ChannelFuture, ChannelFutureListener
}
import io.netty.handler.codec.http.{
  FullHttpRequest, FullHttpResponse,
  DefaultFullHttpResponse, HttpHeaders, HttpResponseStatus, HttpVersion
}

class RequestHandler(server: Server, handler: netcaty.http.RequestHandler, stopAfterOneResponse: Boolean)
  extends SimpleChannelInboundHandler[FullHttpRequest]
{
  override def channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest) {
    val res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    res.headers.set(HttpHeaders.Names.CONTENT_LENGTH, 0)

    handler(req, res)
    val future = ctx.channel.writeAndFlush(res)

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
