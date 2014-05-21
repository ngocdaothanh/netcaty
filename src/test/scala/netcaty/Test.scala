package netcaty

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import io.netty.handler.codec.http.{DefaultFullHttpRequest, FullHttpResponse, HttpHeaders, HttpMethod, HttpVersion}
import io.netty.util.CharsetUtil
import org.scalatest.{FlatSpec, Matchers}

class Test extends FlatSpec with Matchers {
  "Netcaty" should "receive correct response (sync)" in {
    val port = respondOneWithUriInBody()
    val path = "/test"
    val req  = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path)
    val res  = http.request("localhost", port, req)
    val body = readStringContent(res)
    res.release()
    body should be(path)
  }

  "Netcaty" should "receive correct response (async)" in {
    val port        = respondOneWithUriInBody()
    val path        = "/test"
    val req         = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path)
    val bodyPromise = Promise[String]()
    http.request("localhost", port, req, { res =>
      bodyPromise.success(readStringContent(res))
    })
    val body = Await.result(bodyPromise.future, Duration.Inf)
    body should be(path)
  }

  //----------------------------------------------------------------------------

  /** @return Port */
  private def respondOneWithUriInBody(): Int = {
    val server = http.respondOne(0, { case (req, res) =>
      val uri   = req.getUri
      val bytes = uri.getBytes(CharsetUtil.UTF_8)
      res.content.writeBytes(bytes)
      res.headers.set(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    })
    server.getPort
  }

  private def readStringContent(res: FullHttpResponse): String = {
    val bytes = new Array[Byte](res.content.readableBytes)
    res.content.readBytes(bytes)
    new String(bytes, CharsetUtil.UTF_8)
  }
}
