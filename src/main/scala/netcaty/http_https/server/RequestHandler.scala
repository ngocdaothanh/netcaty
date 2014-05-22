package netcaty.http_https.server

import io.netty.channel.{
  ChannelHandlerContext, SimpleChannelInboundHandler,
  ChannelFuture, ChannelFutureListener
}
import io.netty.handler.codec.http.{
  FullHttpRequest, FullHttpResponse,
  DefaultFullHttpResponse, HttpHeaders, HttpResponseStatus, HttpVersion
}

import netcaty.HttpHttps

class RequestHandler(server: Server, handler: HttpHttps.RequestHandler, stopAfterOneResponse: Boolean)
  extends SimpleChannelInboundHandler[FullHttpRequest]
{
  override def channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest) {
    // Automatically handle "Expect 100 Continue" request,
    // the handler doesn't have to handle it
    val continue = HttpHeaders.is100ContinueExpected(req)
    val res = if (continue) {
      new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE)
    } else {
      val ret = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
      ret.headers.set(HttpHeaders.Names.CONTENT_LENGTH, 0)
      handler(req, ret)
      ret
    }

    val future = ctx.channel.writeAndFlush(res)
    if (!continue) {
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
}
