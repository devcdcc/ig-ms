name := "commons"

lazy val commons = project.in(file("."))

/**
  * Resolvers dependencies.
  */
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

/**
  * Library dependencies
  */
//val circeVersion = "0.11.1"
//libraryDependencies ++= Seq(
//  "io.circe" %% "circe-core",
//  "io.circe" %% "circe-generic",
//  "io.circe" %% "circe-parser"
//).map(_ % circeVersion)
libraryDependencies += guice
// https://mvnrepository.com/artifact/com.typesafe.play/play-json
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.2"

val akkaVersion = "2.5.23"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)

libraryDependencies += "org.scalactic" %% "scalactic"     % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest"     % "3.0.5" % Test
libraryDependencies += "org.mockito"   %% "mockito-scala" % "1.1.2" % Test
// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams-scala
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream-kafka
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka"  % "1.0.3"
libraryDependencies += "com.dslplatform"   %% "dsl-json-scala"     % "1.9.3"
libraryDependencies += "org.json4s"        %% "json4s-native"      % "3.6.6"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0"
// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.6"
