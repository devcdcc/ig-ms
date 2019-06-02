name := "ig-http-api"

lazy val commons = project in file("../commons")
lazy val `ig-http-api` = (project in file("."))
  .dependsOn(commons)
  .aggregate(commons)
  .enablePlugins(PlayScala)
