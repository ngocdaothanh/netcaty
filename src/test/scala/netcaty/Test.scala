package netcaty

import io.netty.handler.codec.http.{DefaultFullHttpRequest, HttpHeaders, HttpMethod, HttpVersion}
import org.scalatest.{FlatSpec, Matchers}

class Test extends FlatSpec with Matchers {
  "Netcaty" should "receive correct response" in {
    // Write the request path to the response.
    // The response body should be the path.

    val path = "/test"

    http.respondOne(9000, { case (req, res) =>
      val uri   = req.getUri
      val bytes = uri.getBytes
      res.content.writeBytes(bytes)
      res.headers.set(HttpHeaders.Names.CONTENT_LENGTH, bytes.length)
    })

    val req   = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path)
    val res   = http.request("localhost", 9000, req)
    val bytes = new Array[Byte](res.content.readableBytes)
    res.content.readBytes(bytes)

    val body = new String(bytes)
    body should be(path)
  }
}
