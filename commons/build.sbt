name := "commons"

lazy val commons = project.in(file("."))

libraryDependencies += guice
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % "2.5.23",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.23" % Test
)
val circeVersion = "0.11.1"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams
libraryDependencies += "org.apache.kafka" % "kafka-streams" % "2.2.0"
