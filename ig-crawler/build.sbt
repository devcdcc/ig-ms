name:= "ig-crawler"

lazy val commons = project in file("../commons")
lazy val `ig-crawler` = (project in file("."))
  .dependsOn(commons)
  .aggregate(commons)
