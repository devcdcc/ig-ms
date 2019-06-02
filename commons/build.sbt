name := "commons"

lazy val commons = project.in(file("."))

/**
  * Resolvers dependencies.
  */
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

/**
  * Library dependencies
  */
libraryDependencies += guice
val circeVersion = "0.11.1"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

val akkaVersion = "2.5.23"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams
libraryDependencies += "org.apache.kafka" % "kafka-streams" % "2.2.0"
