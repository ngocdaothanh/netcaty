package netcaty.tcp.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.{Channel, ChannelFuture, ChannelFutureListener}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.concurrent.DefaultPromise

import netcaty.tcp

object Client {
  def request(ssl: Boolean, host: String, port: Int, req: Array[Byte], responseLength: Int): Array[Byte] = {
    val worker     = new NioEventLoopGroup(1)
    val resPromise = new DefaultPromise[Array[Byte]](worker.next())
    val ch = (new Bootstrap)
      .group(worker)
      .channel(classOf[NioSocketChannel])
      .handler(new PipelineInitializer(ssl, responseLength, Left(resPromise)))
      .connect(host, port)
      .sync()  // Wait for the connection to be established
      .channel

    // reqBuf will be automatically released
    val reqBuf = ch.alloc.buffer(req.length).writeBytes(req)
    ch.writeAndFlush(reqBuf)

    resPromise.sync()
    resPromise.get()
  }

  def request(ssl: Boolean, host: String, port: Int, req: Array[Byte], responseLength: Int, handler: tcp.ResponseHandler) {
    (new Bootstrap)
      .group(new NioEventLoopGroup(1))
      .channel(classOf[NioSocketChannel])
      .handler(new PipelineInitializer(ssl, responseLength, Right(handler)))
      .connect(host, port)
      .addListener(new ChannelFutureListener {
        override def operationComplete(future: ChannelFuture) {
          // reqBuf will be automatically released
          val ch     = future.channel
          val reqBuf = ch.alloc.buffer(req.length).writeBytes(req)
          ch.writeAndFlush(reqBuf)
        }
      })
  }
}
