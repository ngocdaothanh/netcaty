package netcaty

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import io.netty.handler.codec.http.{DefaultFullHttpRequest, FullHttpResponse, HttpHeaders, HttpMethod, HttpVersion}
import io.netty.util.CharsetUtil
import org.scalatest.{FlatSpec, Matchers}

class Test extends FlatSpec with Matchers {
  behavior of "Netcaty"

  for (s <- Seq(false, true)) {
    val protocol = if (s) "HTTPS" else "HTTP"

    it should s"receive correct response ($protocol, sync)" in {
      val port = respondOneWithUriInBody(s)
      val path = "/test"
      val req  = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path)
      val o    = if (s) https else http
      val res  = o.request("localhost", port, req)
      val body = readStringContent(res)
      res.release()
      body should be(path)
    }

    it should s"receive correct response ($protocol, async)" in {
      val port        = respondOneWithUriInBody(s)
      val path        = "/test"
      val req         = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path)
      val bodyPromise = Promise[String]()
      val o           = if (s) https else http
      o.request("localhost", port, req, { res =>
        bodyPromise.success(readStringContent(res))
      })
      val body = Await.result(bodyPromise.future, Duration.Inf)
      body should be(path)
    }
  }

  //----------------------------------------------------------------------------

  /** @return Port */
  private def respondOneWithUriInBody(s: Boolean): Int = {
    val handler: HttpHttps.RequestHandler = { (req, res) =>
      val uri   = req.getUri
      val bytes = uri.getBytes(CharsetUtil.UTF_8)
      res.content.writeBytes(bytes)
      res.headers.set(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    }

    val server = if (s) https.respondOne(0, handler) else http.respondOne(0, handler)
    server.getPort
  }

  private def readStringContent(res: FullHttpResponse): String = {
    val bytes = new Array[Byte](res.content.readableBytes)
    res.content.readBytes(bytes)
    new String(bytes, CharsetUtil.UTF_8)
  }
}
