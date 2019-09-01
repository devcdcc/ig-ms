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
libraryDependencies += "com.typesafe.play"       %% "play-json"            % playVersion
libraryDependencies += "org.testcontainers"      % "selenium"              % "1.12.0"
libraryDependencies += "com.dimafeng"            %% "testcontainers-scala" % "0.30.0" % Test
libraryDependencies += "org.seleniumhq.selenium" % "selenium-java"         % "3.141.59"
libraryDependencies += "io.cucumber"             %% "cucumber-scala"       % "4.7.1" % Test

val http4sVersion = "0.20.1"

def http4sLibraries =
  Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion
  )
libraryDependencies += "io.cucumber" %% "cucumber-scala" % "4.3.1" % Test

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.1"

val circeVersion = "0.11.0"

def circeLibraries =
  Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-optics"
  ).map(_ % circeVersion)

val akkaVersion = "2.5.23"

val commonSettings = Seq(
  organization := "com.github.com.devcdcc",
  scalaVersion := projectScalaVersion,
  version := projectVersion,
  scalafmtOnCompile := true,
  libraryDependencies += guice,
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  ),
  libraryDependencies += "org.scalactic" %% "scalactic"     % "3.0.5",
  libraryDependencies += "org.scalatest" %% "scalatest"     % "3.0.5" % Test,
  libraryDependencies += "org.mockito"   %% "mockito-scala" % "1.1.2" % Test,
// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams-scala
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream-kafka
  libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka"  % "1.0.3",
  libraryDependencies += "com.dslplatform"   %% "dsl-json-scala"     % "1.9.3",
  libraryDependencies += "org.json4s"        %% "json4s-native"      % "3.6.6",
  libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
// https://mvnrepository.com/artifact/commons-io/commons-io
  libraryDependencies += "commons-io" % "commons-io" % "2.6",
  libraryDependencies ++= circeLibraries,
  libraryDependencies += "com.typesafe"             % "config"                    % "1.3.3",
  libraryDependencies += "org.scalactic"            %% "scalactic"                % "3.0.5",
  libraryDependencies += "org.scalatest"            %% "scalatest"                % "3.0.5" % Test,
  libraryDependencies += "com.softwaremill.macwire" %% "macros"                   % "2.3.3" % "provided",
  libraryDependencies += "com.softwaremill.macwire" %% "util"                     % "2.3.3",
  libraryDependencies += "org.mockito"              %% "mockito-scala"            % "1.1.2" % Test,
  libraryDependencies += "io.lettuce"               % "lettuce-core"              % "5.1.8.RELEASE",
  libraryDependencies += "io.vertx"                 %% "vertx-redis-client-scala" % "3.8.0",
  libraryDependencies += "io.vertx"                 %% "vertx-lang-scala"         % "3.8.0"
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
    libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.3.0"
  )
  .enablePlugins(ScalafmtPlugin)
  .dependsOn(commons)
  .aggregate(commons)
lazy val `ig-http-api` = (project in file("ig-http-api"))
  .settings(
    libraryDependencies += "com.github.com.devcdcc" %% "publisher-trait"     % "0.1",
    libraryDependencies += "org.apache.kafka"       %% "kafka-streams-scala" % "2.3.0",
    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play"  % "4.0.3" % Test,
    libraryDependencies += "com.dripower"           %% "play-circe"          % "2711.0",
    libraryDependencies += ws
  )
  .settings(commonSettings)
  .dependsOn(`publisher-trait`)
  .aggregate(`publisher-trait`)
  .enablePlugins(ScalafmtPlugin)
  .enablePlugins(PlayScala)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
lazy val `ig-crawler` = (project in file("ig-crawler"))
  .settings(
    libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.3.0",
    libraryDependencies ++= (http4sLibraries ++ circeLibraries),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    // https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams-test-utils
    libraryDependencies += "org.apache.kafka" % "kafka-streams-test-utils" % "2.3.0" % Test
  )
  .dependsOn(commons)
  .aggregate(commons)
  .settings(commonSettings)
  .enablePlugins(ScalafmtPlugin)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .dependsOn(`publisher-trait`, `ig-http-api`, `ig-crawler`)
  .aggregate(`publisher-trait`, `ig-http-api`, `ig-crawler`)
  .enablePlugins(ScalafmtPlugin)

scalafmtOnCompile := true
