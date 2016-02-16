import sbt._

object Dependencies {

  val typesafeConfig  = "com.typesafe" % "config" % "1.3.0"
  val typesafeLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.5"
  // Versions
  val akkaVersion = "2.4.1"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion

  val specs2      = "org.specs2"    %% "specs2-core"  % "3.7.1"     % "test"

  val scodecCore  = "org.scodec"    %% "scodec-core"  % "1.8.3"
  val scodecBits  = "org.scodec"    %% "scodec-bits"  % "1.0.12"
}