HTTP server
-----------

netcatty.http.respond(9000, { case (req, res) =>

})

HTTP client
-----------

netcatty.http.request("localhost", 9000, req, { res =>

})
