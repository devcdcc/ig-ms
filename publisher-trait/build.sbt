name := "publisher-trait"
// https://mvnrepository.com/artifact/com.dslplatform/dsl-json-scala
libraryDependencies += "com.dslplatform" %% "dsl-json-scala" % "1.9.3"
libraryDependencies += "org.scalatest"   %% "scalatest"      % "3.0.5" % Test
// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams-scala
libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "2.1.1"

lazy val commons = project in file("../commons")
lazy val `publisher-trait` = (project in file("."))
  .dependsOn(commons)
  .aggregate(commons)
