package netcaty.http.server

import java.net.InetSocketAddress
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.{FullHttpRequest, FullHttpResponse}

/**
 * @param port 0 means random open port; call realPort after starting to get the real port
 */
class Server(port: Int, handler: netcaty.http.RequestHandler) {
  private var bossGroup:     NioEventLoopGroup = _
  private var workerGroup:   NioEventLoopGroup = _
  private var serverChannel: Channel           = _
  private var realPort:      Int               = _

  /** @return Port number */
  def start(stopAfterOneResponse: Boolean) = {
    bossGroup     = new NioEventLoopGroup(1)
    workerGroup   = new NioEventLoopGroup
    serverChannel = (new ServerBootstrap)
      .group(bossGroup, workerGroup)
      .channel(classOf[NioServerSocketChannel])
      .childHandler(new PipelineInitializer(this, handler, stopAfterOneResponse))
      .bind(port)
      .sync()  // Wait for the port to be bound
      .channel
    realPort      = serverChannel.localAddress.asInstanceOf[InetSocketAddress].getPort
  }

  def getPort = realPort

  def stop() {
    serverChannel.close().sync()
    bossGroup.shutdownGracefully().sync()
    workerGroup.shutdownGracefully().sync()
  }
}
