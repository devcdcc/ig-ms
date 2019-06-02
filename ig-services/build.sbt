name:= "ig-services"

lazy val commons = project in file("../commons")
lazy val services = (project in file("."))
  .dependsOn(commons)
  .aggregate(commons)
