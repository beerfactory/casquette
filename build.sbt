import Dependencies._

scalacOptions in Test ++= Seq("-Yrangepos")

lazy val commonSettings = Seq(
  organization := "org.beerfactory",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val commonDeps =
  compile_dependencies(typesafeLogging, logbackClassic)


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
    libraryDependencies ++= commonDeps ++
      compile_dependencies(akkaActor, scodecCore, scodecBits) ++
      test_dependencies(specs2)
  )

lazy val client = (project in file("client")).
  settings(commonSettings: _*).
  settings(
    name := "client",
    libraryDependencies ++= commonDeps ++
      compile_dependencies(akkaActor, typesafeConfig) ++
      test_dependencies(specs2)
  )
