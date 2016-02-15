import sbt._

object Dependencies {
  // Versions
  val akkaVersion = "2.4.1"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion

  val specs2      = "org.specs2"    %% "specs2-core"  % "3.7.1"     % "test"

  val scodecCore  = "org.scodec"    %% "scodec-core"  % "1.8.3"
  val scodecBits  = "org.scodec"    %% "scodec-bits"  % "1.0.12"
}