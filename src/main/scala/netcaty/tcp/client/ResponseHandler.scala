package netcaty.tcp.client

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.util.concurrent.Promise
import netcaty.tcp

class ResponseHandler(
  resPromise_or_handler: Either[Promise[Array[Byte]], tcp.ResponseHandler]
) extends SimpleChannelInboundHandler[ByteBuf]
{
  override def channelRead0(ctx: ChannelHandlerContext, res: ByteBuf) {
    resPromise_or_handler match {
      case Left(resPromise) =>
        val bytes = new Array[Byte](res.readableBytes)
        res.readBytes(bytes)
        resPromise.setSuccess(bytes)

      case Right(handler) =>
        val bytes = new Array[Byte](res.readableBytes)
        res.readBytes(bytes)
        handler(bytes)
    }

    val ch = ctx.channel
    if (ch.isOpen) ch.close()
  }
}
