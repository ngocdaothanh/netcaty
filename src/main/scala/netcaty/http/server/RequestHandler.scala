package netcaty.http.server

import io.netty.channel.{
  ChannelHandlerContext, SimpleChannelInboundHandler,
  ChannelFuture, ChannelFutureListener
}
import io.netty.handler.codec.http.{
  FullHttpRequest, FullHttpResponse,
  DefaultFullHttpResponse, HttpHeaders, HttpResponseStatus, HttpVersion
}

import netcaty.http

class RequestHandler(server: Server, handler: http.RequestHandler, stopAfterOneResponse: Boolean)
  extends SimpleChannelInboundHandler[FullHttpRequest]
{
  override def channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest) {
    // HttpObjectAggregator automatically sends "Continue" response for
    // "Expect 100 Continue" request.
    //
    // Experiment: curl -v -F name=somevalue http://localhost:9000
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
