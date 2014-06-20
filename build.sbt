organization := "tv.cntt"

name         := "netcaty"

version      := "1.3-SNAPSHOT"

scalaVersion := "2.11.0"

crossScalaVersions := Seq("2.10.4", "2.11.0")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

//------------------------------------------------------------------------------

libraryDependencies += "io.netty" % "netty-all" % "4.0.20.Final"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
