package netcaty

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import io.netty.handler.codec.http.{DefaultFullHttpRequest, FullHttpResponse, HttpHeaders, HttpMethod, HttpVersion}
import org.scalatest.{FlatSpec, Matchers}

class Test extends FlatSpec with Matchers {
  "Netcaty" should "receive correct response (sync)" in {
    respondOneWithUriInBody()

    val path = "/test"
    val req  = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path)
    val res  = http.request("localhost", 9000, req)
    val body = readStringContent(res)
    res.release()
    body should be(path)
  }

  "Netcaty" should "receive correct response (async)" in {
    respondOneWithUriInBody()

    val path        = "/test"
    val req         = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path)
    val bodyPromise = Promise[String]()
    http.request("localhost", 9000, req, { res =>
      bodyPromise.success(readStringContent(res))
    })

    val body = Await.result(bodyPromise.future, Duration.Inf)
    body should be(path)
  }

  //----------------------------------------------------------------------------

  private def respondOneWithUriInBody() {
    http.respondOne(9000, { case (req, res) =>
      val uri   = req.getUri
      val bytes = uri.getBytes
      res.content.writeBytes(bytes)
      res.headers.set(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    })
  }

  private def readStringContent(res: FullHttpResponse): String = {
    val bytes = new Array[Byte](res.content.readableBytes)
    res.content.readBytes(bytes)
    new String(bytes)
  }
}
