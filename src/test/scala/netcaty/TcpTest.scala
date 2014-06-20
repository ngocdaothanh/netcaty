package netcaty

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import io.netty.util.CharsetUtil
import org.scalatest.{FlatSpec, Matchers}

class TcpTest extends FlatSpec with Matchers {
  behavior of "TCP(S)"

  for (ssl <- Seq(false, true)) {
    val protocol = if (ssl) "TCPS" else "TCP"

    val reqStr   = "Hello World"
    val req      = reqStr.getBytes
    val reqLen   = req.length
    val resLen   = reqLen

    it should s"receive correct response ($protocol, sync)" in {
      val port = respondOneWithLowerCase(req.length, ssl)

      val o    = if (ssl) Tcps else Tcp
      val res  = o.request("localhost", port, req, resLen)
      val body = new String(res)

      body should === (reqStr.toLowerCase)
    }

    it should s"receive correct response ($protocol, async)" in {
      val port        = respondOneWithLowerCase(req.length, ssl)

      val bodyPromise = Promise[String]()
      val o           = if (ssl) Tcps else Tcp
      o.request("localhost", port, req, resLen, { res =>
        bodyPromise.success(new String(res))
      })
      val body        = Await.result(bodyPromise.future, Duration.Inf)

      body should === (reqStr.toLowerCase)
    }
  }

  //----------------------------------------------------------------------------

  /** @return Port */
  private def respondOneWithLowerCase(requestLength: Int, ssl: Boolean): Int = {
    val handler = { req: Array[Byte] =>
      val string = new String(req)
      string.toLowerCase.getBytes
    }

    if (ssl) Tcps.respondOne(requestLength, handler) else Tcp.respondOne(requestLength, handler)
  }
}
