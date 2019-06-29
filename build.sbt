name := "ig-ms"
val projectVersion      = "0.1"
val projectScalaVersion = "2.12.8"

/**
  * Resolvers dependencies.
  */
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

/**
  * Library dependencies
  */
libraryDependencies += guice
// https://mvnrepository.com/artifact/com.typesafe.play/play-json
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.2"

val circeVersion = "0.11.0"
// https://mvnrepository.com/artifact/org.http4s/http4s-circe
val http4sVersion = "0.20.1"
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion
)

libraryDependencies += "io.cucumber" %% "cucumber-scala" % "4.3.1" % Test

val akkaVersion = "2.5.23"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream-kafka
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.1"

val commonSettings = Seq(
  organization := "com.github.devcdcc",
  scalaVersion := projectScalaVersion,
  version := projectVersion,
  scalafmtOnCompile := true,
  libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-optics"
  ).map(_ % circeVersion),
  libraryDependencies += "org.scalactic" %% "scalactic"     % "3.0.5",
  libraryDependencies += "org.scalatest" %% "scalatest"     % "3.0.5" % Test,
  libraryDependencies += "org.mockito"   %% "mockito-scala" % "1.1.2" % Test
)

lazy val commons = (project in file("commons"))
  .settings(commonSettings)
  .enablePlugins(ScalafmtPlugin)

lazy val `publisher-trait` = (project in file("publisher-trait"))
  .settings(commonSettings)
  .enablePlugins(ScalafmtPlugin)
  .dependsOn(commons)
  .aggregate(commons)

lazy val `ig-http-api` = (project in file("ig-http-api"))
  .settings(commonSettings)
  .enablePlugins(ScalafmtPlugin)
  .enablePlugins(PlayScala)
  .dependsOn(`publisher-trait`)
  .aggregate(`publisher-trait`)

lazy val `ig-crawler` = (project in file("ig-crawler"))
  .settings(commonSettings)
  .enablePlugins(ScalafmtPlugin)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .dependsOn(`publisher-trait`, `ig-http-api`, `ig-crawler`)
  .aggregate(`publisher-trait`, `ig-http-api`, `ig-crawler`)
  .enablePlugins(ScalafmtPlugin)

scalafmtOnCompile := true
