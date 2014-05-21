package netcaty.http.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}

class Server(port: Int, handler: netcaty.http.RequestHandler) {
  private var bossGroup:     NioEventLoopGroup = _
  private var workerGroup:   NioEventLoopGroup = _
  private var serverChannel: Channel           = _

  def start(stopAfterOneResponse: Boolean) {
    bossGroup     = new NioEventLoopGroup(1)
    workerGroup   = new NioEventLoopGroup
    serverChannel = (new ServerBootstrap)
      .group(bossGroup, workerGroup)
      .channel(classOf[NioServerSocketChannel])
      .childHandler(new PipelineInitializer(this, handler, stopAfterOneResponse))
      .bind(port)
      .sync()  // Wait for the port to be bound
      .channel
  }

  def stop() {
    serverChannel.close().sync()
    bossGroup.shutdownGracefully().sync()
    workerGroup.shutdownGracefully().sync()
  }
}
