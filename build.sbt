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
val playVersion = "2.7.3"
libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion
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

val circeVersion = "0.11.0"

val commonSettings = Seq(
  organization := "com.github.com.devcdcc",
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
) // https://mvnrepository.com/artifact/com.dripower/play-circe

lazy val commons = (project in file("commons"))
  .settings(commonSettings)
  .enablePlugins(ScalafmtPlugin)

lazy val `publisher-trait` = (project in file("publisher-trait"))
  .settings(commonSettings)
  .settings(
    libraryDependencies += "com.dslplatform" %% "dsl-json-scala" % "1.9.3",
    libraryDependencies += "org.scalatest"   %% "scalatest"      % "3.0.5" % Test,
    // https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams-scala
    libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.1.1"
  )
  .enablePlugins(ScalafmtPlugin)
  .dependsOn(commons)
  .aggregate(commons)
lazy val `ig-http-api` = (project in file("ig-http-api"))
  .settings(
    libraryDependencies += "com.github.com.devcdcc" %% "publisher-trait"     % "0.1",
    libraryDependencies += "org.apache.kafka"       %% "kafka-streams-scala" % "2.1.1",
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play"  % "4.0.3" % Test,
    libraryDependencies += "com.dripower"           %% "play-circe"          % "2711.0",
    libraryDependencies += ws
  )
  .settings(commonSettings)
  .dependsOn(`publisher-trait`)
  .aggregate(`publisher-trait`)
  .enablePlugins(ScalafmtPlugin)
  .enablePlugins(PlayScala)

lazy val `ig-crawler` = (project in file("ig-crawler"))
  .settings(commonSettings)
  .enablePlugins(ScalafmtPlugin)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .dependsOn(`publisher-trait`, `ig-http-api`, `ig-crawler`)
  .aggregate(`publisher-trait`, `ig-http-api`, `ig-crawler`)
  .enablePlugins(ScalafmtPlugin)

scalafmtOnCompile := true
