organization := "pl.matisoft"

name := "swagger-play24"

scalaVersion := "2.10.5"

crossScalaVersions := Seq("2.10.5", "2.11.6")

//Play 2.4 only support Java 8 anyway
javacOptions ++= Seq("-target", "1.8", "-source", "1.8")

lazy val main = (project in file("."))
                .enablePlugins(PlayScala)

libraryDependencies += "com.wordnik" %% "swagger-core" % "1.3.12"

libraryDependencies += "com.wordnik" %% "swagger-jaxrs" % "1.3.12"

releaseCrossBuild := true

licenses := Seq("Apache-style" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

releasePublishArtifactsAction := PgpKeys.publishSigned.value