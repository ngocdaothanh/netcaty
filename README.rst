This Scala library is convenient for creating network server and client,
useful for quickly writing server and client tests. This library doesn't try to
be robust. If you want long running robust server or client, you should try
other things.

This library is small, the only dependency is `Netty <http://netty.io/>`_.

Features
--------

HTTP:

* Server
* Client
* Can handle chunks up to 16 MB

TODO (pull requests are welcome):

* HTTPS
* TCP

`Scaladoc <http://ngocdaothanh.github.io/netcaty/#netcaty.http.package>`_

Be familiar with Netty
----------------------

To create and inspect requests/responses, you should be familiar with things in
package `io.netty.handler.codec.http <http://netty.io/4.0/api/io/netty/handler/codec/http/package-frame.html>`_
and `io.netty.buffer <http://netty.io/4.0/api/io/netty/buffer/package-frame.html>`_
in `Netty Javadoc <http://netty.io/4.0/api/index.html>`_.

``req`` and ``res`` in the examples below are:

* ``req``: `FullHttpRequest <http://netty.io/4.0/api/io/netty/handler/codec/http/FullHttpRequest.html>`_
* ``res``: `FullHttpResponse <http://netty.io/4.0/api/io/netty/handler/codec/http/FullHttpResponse.html>`_

HTTP server
-----------

::

  netcaty.http.respondOne(9000, { case (req, res) =>
    // res is an empty 200 OK response.
    // Modify it to respond what you want.
  })

``respondOnce`` returns after the port has been bounded so you don't need to
manually call ``Thread.sleep(someTime)`` to wait for the server to be started.
The server runs on a separate thread. It sends only one response and after that
stops immediately.

If you don't want to stop the server after one response:

::

  val server = netcaty.http.respond(9000, { case (req, res) =>
    // res is an empty 200 OK response.
    // Modify it to respond what you want.
  })

  // Later:
  server.stop()

HTTP client
-----------

Sync mode:

::

  // Create a FullHttpRequest
  import io.netty.handler.codec.http.{DefaultFullHttpRequest, HttpMethod, HttpVersion}
  val req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/path")

  // req will be automatically released
  val res = netcaty.http.request("localhost", 9000, req)

  // Use res
  ...

  // Must manually release after using
  res.release()

Async mode:

::

  // Create a FullHttpRequest
  import io.netty.handler.codec.http.{DefaultFullHttpRequest, HttpMethod, HttpVersion}
  val req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/path")

  // req and res will be automatically released
  netcaty.http.request("localhost", 9000, req, { res =>
    ...
  })

Use with SBT
------------

Supported Scala versions: 2.10.x, 2.11.x

::

  libraryDependencies += "tv.cntt" % "netcaty" %% "1.0"

Netcat
------

For more simple problems, maybe you don't need to use additionaly library. You
can use `Netcat <http://en.wikipedia.org/wiki/Netcat>`_, like this:

::

  import scala.sys.process._

  object Http {
    def async(fun: => Unit) {
      val t = new Thread(new Runnable { def run { fun } })
      t.start()
    }

    //----------------------------------------------------------------------------

    def serveRaw(port: Int, lines: Seq[String]) {
      val raw = lines.mkString("\r\n")
      (Seq("echo", "-n", raw) #| Seq("sh", "-c", "nc -l " + port)).!
    }

    def serveContent(port: Int, contentType: String, content: String) {
      val contentLength = content.getBytes.length
      serveRaw(port, Seq(
        "HTTP/1.1 200 OK",
        s"Content-Type: $contentType",
        s"Content-Length: $contentLength",
        "",
        content
      ))
    }

    def asyncServeRaw(port: Int, lines: Seq[String]) {
      async { serveRaw(port, lines) }
    }

    def asyncServeContent(port: Int, contentType: String, content: String) {
      async { serveContent(port, contentType, content) }
    }

    //----------------------------------------------------------------------------

    def requestRaw(host: String, port: Int, lines: Seq[String]): String = {
      val raw = lines.mkString("", "\r\n", "\r\n\r\n")
      // "-i 1" delays 1s, slowering the tests.
      // But without it the result will be empty.
      (Seq("echo", "-n", raw) #| s"nc -i 1 $host $port").!!
    }

    def get(host: String, port: Int, path: String): String = {
      requestRaw(host, port, Seq(
        s"GET $path HTTP/1.1",
        s"Host: $host:$port"
      ))
    }
  }
