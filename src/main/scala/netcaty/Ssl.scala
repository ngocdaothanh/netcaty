package netcaty

import io.netty.handler.ssl.{SslContext, SslProvider}
import io.netty.handler.ssl.util.{InsecureTrustManagerFactory, SelfSignedCertificate}

// See: https://github.com/netty/netty/tree/master/example/src/main/java/io/netty/example/securechat
object Ssl {
  val serverContext = {
    val ssc = new SelfSignedCertificate
    SslContext.newServerContext(SslProvider.JDK, ssc.certificate, ssc.privateKey)
  }

  val clientContext =
    SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE)
}
