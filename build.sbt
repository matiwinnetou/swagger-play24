organization := "pl.matisoft"

name := "swagger-play24"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.5"

crossScalaVersions := Seq("2.10.5", "2.11.6")

publishMavenStyle := true

val commonSettings = Seq(
    javacOptions ++= Seq("-target", "1.8", "-source", "1.8"),
    parallelExecution := true
)

lazy val main = (project in file("."))
                .settings(commonSettings:_*)
                .enablePlugins(PlayScala)

libraryDependencies += "com.wordnik" %% "swagger-core" % "1.3.12"

libraryDependencies += "com.wordnik" %% "swagger-jaxrs" % "1.3.12"

publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }

licenses := Seq("Apache-style" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

pomIncludeRepository := { _ => false }

pomExtra := (
    <scm>
      <url>git@github.com:matiwinnetou/swagger-play24.git</url>
      <connection>scm:git:git@github.com:matiwinnetou/swagger-play24.git</connection>
    </scm>
  <url>https://github.com/swagger-play24/swagger-play24</url>
  <developers>
      <developer>
          <id>matiwinnetou</id>
          <name>Mateusz Szczap</name>
          <url>https://github.com/swagger-play24</url>
        </developer>
  </developers>)
