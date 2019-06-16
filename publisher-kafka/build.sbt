name := "publisher-trait"
// https://mvnrepository.com/artifact/com.dslplatform/dsl-json-scala
libraryDependencies += "com.dslplatform" %% "dsl-json-scala" % "1.9.3"

lazy val commons = project in file("../commons")
lazy val `publisher-trait` = (project in file("."))
  .dependsOn(commons)
  .aggregate(commons)
