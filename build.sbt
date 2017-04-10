import sbt.project
import sbt._
import sbt.ExclusionRule
import sbt.Keys._
import sbtassembly.AssemblyPlugin.autoImport._

name := "parquet-demo"

lazy val commonSettings = Seq(
  organization :="ch.unibas.dmi.hs17.dis",
  scalaVersion := "2.11.8"
)

//build
lazy val buildSettings = Seq(
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.11.8"),
  ivyScala := ivyScala.value.map(_.copy(overrideScalaVersion = true))
)

//projects
  lazy val root = (project in file(".")).
  settings(commonSettings: _*)

//assembly
assemblyOption in assembly :=
  (assemblyOption in assembly).value.copy(includeScala = true)

//mainClass in(Compile, run) := Some("ch.unibas.dmi.dbis.adam.main.Startup")

unmanagedResourceDirectories in Compile += baseDirectory.value / "conf"
scalacOptions ++= Seq("-target:jvm-1.7")

//lib resolvers
resolvers ++= Seq(
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Restlet Repositories" at "http://maven.restlet.org"
)
resolvers += Resolver.sonatypeRepo("snapshots")

//base libs
val baseLibs = Seq(
  "org.scala-lang" % "scala-compiler" % "2.11.8",
  "org.scala-lang" % "scala-reflect" % "2.11.8"
)

//libs
val coreLibs = Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.1.0" excludeAll ExclusionRule("org.apache.hadoop"), //make sure that you use the same spark version as in your deployment!
  "org.apache.spark" % "spark-sql_2.11" % "2.1.0",
  "org.apache.spark" % "spark-hive_2.11" % "2.1.0",
  "org.apache.hadoop" % "hadoop-client" % "2.7.1" excludeAll ExclusionRule("javax.servlet") //make sure that you use the same hadoop version as in your deployment!
).map(
  _.excludeAll(
    ExclusionRule("org.scala-lang"),
    ExclusionRule("org.slf4j", "slf4j-api")
  )
)

//secondary libs
val secondaryLibs = Seq(
  "it.unimi.dsi" % "fastutil" % "7.0.12",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.apache.commons" % "commons-math3" % "3.4.1",
  "com.google.guava" % "guava" % "21.0"
).map(
  _.excludeAll(
    ExclusionRule("org.scala-lang"),
    ExclusionRule("org.slf4j", "slf4j-api")
  )
)

//log libs
val logLibs = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.slf4j" % "slf4j-log4j12" % "1.7.25"
)

libraryDependencies := baseLibs ++ coreLibs ++ secondaryLibs ++ logLibs

unmanagedBase <<= baseDirectory { base => base / "lib" }
unmanagedResourceDirectories in Compile += baseDirectory.value / "conf"

//assembly
assemblyOption in assembly :=
  (assemblyOption in assembly).value.copy(includeScala = false)

val meta = """META.INF(.)*""".r
assemblyMergeStrategy in assembly := {
  case x if x.contains("slf4j-api") => MergeStrategy.last
  case x if x.contains("org.slf4j") => MergeStrategy.first
  case x if x.contains("org.apache.httpcomponents") => MergeStrategy.last
  case x if x.contains("org.apache.commons") => MergeStrategy.last
  case PathList("application.conf") => MergeStrategy.discard
  case PathList("javax", "servlet", xs@_*) => MergeStrategy.last
  case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.last
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.last
}

//mainClass in assembly := Some("ch.unibas.dmi.dbis.adam.main.Startup")

//test in assembly := {}

//provided libraries should be included in "run"
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in(Compile, run), runner in(Compile, run))

lazy val setupDocker = taskKey[Unit]("Setup docker containers for ADAMpro to run.")
setupDocker := {
  "./scripts/docker-setup.sh" !
}

lazy val destroyDocker = taskKey[Unit]("Destroys docker containers for ADAMpro (careful: this command deletes the containers!).")
destroyDocker := {
  "./scripts/docker-destroy.sh" !
}

lazy val startDocker = taskKey[Unit]("Starts the docker containers for ADAMpro.")
startDocker := {
  "./scripts/docker-start.sh" !
}

lazy val stopDocker = taskKey[Unit]("Stops the docker containers for ADAMpro.")
stopDocker := {
  "./scripts/docker-stop.sh" !
}

lazy val runDocker = taskKey[Unit]("Runs ADAMpro in docker container.")
runDocker := {
  //TODO: check that docker is running before running assembly and submitting
  assembly.value
  "./scripts/docker-run.sh" !
}

lazy val buildDocker = taskKey[Unit]("Builds the image of a self-contained docker container.")
buildDocker := {
  assembly.value
  "./scripts/docker-build.sh" !
}
