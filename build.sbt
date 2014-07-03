organization := "tv.cntt"

name         := "netcaty"

version      := "1.4-SNAPSHOT"

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.10.4", "2.11.1")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

//------------------------------------------------------------------------------

libraryDependencies += "io.netty" % "netty-all" % "4.0.21.Final"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test"
