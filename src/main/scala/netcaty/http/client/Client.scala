package netcaty.http.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.{ChannelFuture, ChannelFutureListener}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}
import io.netty.util.concurrent.DefaultPromise

import netcaty.http

object Client {
  def request(https: Boolean, host: String, port: Int, req: FullHttpRequest): FullHttpResponse = {
    val worker     = new NioEventLoopGroup(1)
    val resPromise = new DefaultPromise[FullHttpResponse](worker.next())
    val ch = (new Bootstrap)
      .group(worker)
      .channel(classOf[NioSocketChannel])
      .handler(new PipelineInitializer(https, Left(resPromise)))
      .connect(host, port)
      .sync()  // Wait for the connection to be established
      .channel

    // req will be automatically released
    ch.writeAndFlush(req)

    resPromise.sync()
    resPromise.get()
  }

  def request(https: Boolean, host: String, port: Int, req: FullHttpRequest, handler: http.ResponseHandler) {
    (new Bootstrap)
      .group(new NioEventLoopGroup(1))
      .channel(classOf[NioSocketChannel])
      .handler(new PipelineInitializer(https, Right(handler)))
      .connect(host, port)
      .addListener(new ChannelFutureListener {
        override def operationComplete(future: ChannelFuture) {
          // req will be automatically released
          future.channel.writeAndFlush(req)
        }
      })
  }
}
