This project provides some network tools for Scala as simple to use as the
``nc`` (`Netcat <http://en.wikipedia.org/wiki/Netcat>`_) comand.

They are convenient for developers who knows Netty and Scala.
They are useful for testing network servers/clients.

HTTP server
-----------

::

  netcatty.http.respondOne(9000, { case (req, res) =>
    // res is an empty 200 OK response.
    // Modify it to respond what you want.
  })

* req: `FullHttpRequest <http://netty.io/4.0/api/io/netty/handler/codec/http/FullHttpRequest.html>`_
* res: `FullHttpResponse <http://netty.io/4.0/api/io/netty/handler/codec/http/FullHttpResponse.html>`_

HTTP client
-----------

::

  val req = ...  // Create a FullHttpRequest
  netcatty.http.request("localhost", 9000, req, { res =>
    // Do whatever you want with the response here.
  })

Use with SBT
------------

Supported Scala versions: 2.10.x, 2.11.x

::

  libraryDependencies += "tv.cntt" % "netcatty" %% "1.0"
