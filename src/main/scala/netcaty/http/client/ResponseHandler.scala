package netcaty.http.client

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.util.concurrent.Promise
import netcaty.http

class ResponseHandler(resPromise_or_handler: Either[Promise[FullHttpResponse], http.ResponseHandler])
  extends SimpleChannelInboundHandler[FullHttpResponse]
{
  override def channelRead0(ctx: ChannelHandlerContext, res: FullHttpResponse) {
    resPromise_or_handler match {
      case Left(resPromise) =>
        // Retain because SimpleChannelInboundHandler will automatically release res
        resPromise.setSuccess(res.retain())

      case Right(handler) =>
        handler(res)
    }

    val ch = ctx.channel
    if (ch.isOpen) ch.close()
  }
}
