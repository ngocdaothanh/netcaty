This library provides some methods as easy to use as the
``nc`` (`Netcat <http://en.wikipedia.org/wiki/Netcat>`_) comand.

They are convenient for developers who knows Netty and Scala.
They are useful for writing servers/client tests.

This Scala library is small, the only dependency is Netty.

HTTP server
-----------

::

  netcaty.http.respondOne(9000, { case (req, res) =>
    // res is an empty 200 OK response.
    // Modify it to respond what you want.
  })

* req: `FullHttpRequest <http://netty.io/4.0/api/io/netty/handler/codec/http/FullHttpRequest.html>`_
* res: `FullHttpResponse <http://netty.io/4.0/api/io/netty/handler/codec/http/FullHttpResponse.html>`_

``respondOnce`` returns after the port has been bounded so you don't need to
manually call ``Thread.sleep(someTime)`` to wait for the server to be started.
The server runs on a separate thread. It sends only one response and after that
stops immediately.

To respond multiple responses:

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

  val req = ...  // Create a FullHttpRequest
  val res = netcaty.http.request("localhost", 9000, req)
  // Do whatever you want with the response here.

Async mode:

::

  val req = ...  // Create a FullHttpRequest
  netcaty.http.request("localhost", 9000, req, { res =>
    // Do whatever you want with the response here.
  })

Use with SBT
------------

Supported Scala versions: 2.10.x, 2.11.x

::

  libraryDependencies += "tv.cntt" % "netcaty" %% "1.0"
