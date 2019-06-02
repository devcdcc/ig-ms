name := "ig-ms"
version := "0.1"

val commonSettings = Seq(
  organization := "com.github.devcdcc",
  scalaVersion := "2.12.8",
  scalafmtOnCompile := true
)

lazy val commons = (project in file("commons"))
  .settings(commonSettings)

lazy val services = (project in file("ig-services"))
  .settings(commonSettings)
  .dependsOn(commons)
  .aggregate(commons)

lazy val `ig-http-api` = (project in file("ig-http-api"))
  .dependsOn(commons)
  .aggregate(commons)
  .settings(commonSettings)
  .enablePlugins(PlayScala)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .dependsOn(commons, services, `ig-http-api`)
  .aggregate(commons, services, `ig-http-api`)
  .enablePlugins(ScalafmtPlugin)
