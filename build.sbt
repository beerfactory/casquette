import Dependencies._

scalacOptions in Test ++= Seq("-Yrangepos")

lazy val commonSettings = Seq(
  organization := "org.beerfactory",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  aggregate(core).
  settings(commonSettings: _*).
  settings(
    name := "casquette"
  )

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "core",
    libraryDependencies ++= Seq(akkaActor, specs2, scodecCore, scodecBits)
  )

lazy val client = (project in file("client")).
  settings(commonSettings: _*).
  settings(
    name := "client",
    libraryDependencies ++= Seq(akkaActor, specs2, logbackClassic, typesafeConfig, typesafeLogging)
  )
