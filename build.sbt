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
  aggregate(casquetteMQTT, casquetteCore, client).
  settings(commonSettings: _*).
  settings(
    name := "casquette"
  )

lazy val casquetteMQTT = (project in file("casquette-mqtt")).
  settings(commonSettings: _*).
  settings(
    name := "casquette-mqtt",
    libraryDependencies ++= commonDeps ++
      compile_dependencies(scodecCore, scodecBits) ++
      test_dependencies(specs2)
  )

lazy val casquetteCore = (project in file("casquette-core")).
  dependsOn(casquetteMQTT).
  settings(commonSettings: _*).
  settings(
    name := "casquette-core",
    libraryDependencies ++= commonDeps ++
      compile_dependencies(akkaActor, akkaStream, scodecCore, scodecBits) ++
      test_dependencies(specs2, akkaTestKit, akkaStreamTestKit)
  )

lazy val client = (project in file("client")).
  dependsOn(casquetteMQTT, casquetteCore).
  settings(commonSettings: _*).
  settings(
    name := "client",
    libraryDependencies ++= commonDeps ++
      compile_dependencies(akkaActor, akkaStream, scodecCore, scodecBits, typesafeConfig) ++
      test_dependencies(specs2, akkaTestKit, akkaStreamTestKit)
  )
