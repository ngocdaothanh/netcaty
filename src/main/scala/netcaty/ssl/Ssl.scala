package netcaty.ssl

import java.security.{KeyStore => JKeyStore}
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.X509TrustManager
import io.netty.handler.ssl.SslHandler

// See https://github.com/netty/netty/tree/master/example/src/main/java/io/netty/example/securechat
object Ssl {
  /** SSL handler cannot be shared. */
  def createClientHandler() = new SslHandler(createClientEngine())

  /** SSL handler cannot be shared. */
  def createServerHandler() = new SslHandler(createServerEngine())

  //----------------------------------------------------------------------------

  private val PROTOCOL      = "TLS"
  private val ALGORITHM     = "SunX509"
  private val KEYSTORE_TYPE = "JKS"

  private val GULLIBLE_TRUST_MANAGER = new X509TrustManager {
    def getAcceptedIssuers: Array[X509Certificate] = Array[X509Certificate]()
    def checkClientTrusted(chain: Array[X509Certificate], authType: String) {}
    def checkServerTrusted(chain: Array[X509Certificate], authType: String) {}
  }

  // Context can be resused.
  private val clientContext: SSLContext = {
    val ret = SSLContext.getInstance(PROTOCOL)
    ret.init(null, Array(GULLIBLE_TRUST_MANAGER), null)
    ret
  }

  // Context can be resused.
  private val serverContext: SSLContext = {
    val ks = JKeyStore.getInstance(KEYSTORE_TYPE)
    ks.load(KeyStore.asInputStream, KeyStore.KEY_STORE_PASSWORD)

    // Set up key manager factory to use our key store
    val kmf = KeyManagerFactory.getInstance(ALGORITHM)
    kmf.init(ks, KeyStore.CERTIFICATE_PASSWORD)

    // Initialize the SSLContext to work with our key managers
    val ret = SSLContext.getInstance(PROTOCOL)
    ret.init(kmf.getKeyManagers, null, null)
    ret
  }

  // Engine must be recreated everytime.
  private def createClientEngine(): SSLEngine = {
    val ret = clientContext.createSSLEngine()
    ret.setUseClientMode(true)
    ret
  }

  // Engine must be recreated everytime.
  private def createServerEngine(): SSLEngine = {
    val ret = serverContext.createSSLEngine()
    ret.setUseClientMode(false)
    ret
  }
}
