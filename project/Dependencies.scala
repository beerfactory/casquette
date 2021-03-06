import sbt._

object Dependencies {
  // Versions
  val akkaVersion = "2.4.2"


  val typesafeConfig  = "com.typesafe" % "config" % "1.3.0"
  val typesafeLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.1.5"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaStream = "com.typesafe.akka" % "akka-stream_2.11" % akkaVersion
  val akkaTestKit = "com.typesafe.akka" % "akka-testkit_2.11" % akkaVersion
  val akkaStreamTestKit = "com.typesafe.akka" % "akka-stream-testkit_2.11" % akkaVersion
  val specs2      = "org.specs2"    %% "specs2-core"  % "3.7.1"

  val scodecCore  = "org.scodec"    %% "scodec-core"  % "1.8.3"
  val scodecBits  = "org.scodec"    %% "scodec-bits"  % "1.0.12"

  def compile_dependencies   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided_dependencies  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test_dependencies      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime_dependencies   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container_dependencies (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")
  def optional_dependencies  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile,optional")
}