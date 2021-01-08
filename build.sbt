import com.typesafe.sbt.packager.docker.Cmd

import scala.io.Source
import scala.util.parsing.json.JSON

name := "codacy-roslyn"

ThisBuild / scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.codacy" %% "codacy-engine-scala-seed" % "5.0.3",
  "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
  "com.lihaoyi" %% "ujson" % "1.2.2",
  "org.scalatest" %% "scalatest" % "3.2.2" % Test
)

val staticcheckVersion = "2020.1.6"

dependsOn(shared)

lazy val shared = project
  .settings(
    libraryDependencies += "com.codacy" %% "codacy-analysis-cli-model" % "5.1.0"
  )

lazy val `doc-generator` = project
  .settings(
    Compile / sourceGenerators += Def.task {
      val file = (Compile / sourceManaged).value / "codacy" / "staticcheck" / "Versions.scala"
      IO.write(file, s"""package com.codacy.roslyn
                        |object Versions {
                        |  val staticcheckVersion: String = "$staticcheckVersion"
                        |}
                        |""".stripMargin)
      Seq(file)
    }.taskValue,
    libraryDependencies ++= Seq(
      "com.github.pathikrit" %% "better-files" % "3.9.1",
      "com.lihaoyi" %% "ujson" % "1.2.2",
    )
  )
  .dependsOn(shared)

enablePlugins(GraalVMNativeImagePlugin)

val graalVersion = "20.2.0-java11"

// Graal vm build options
graalVMNativeImageGraalVersion := Some(graalVersion)
containerBuildImage := Some(s"oracle/graalvm-ce:$graalVersion")
graalVMNativeImageOptions ++= Seq(
  "-O1",
  "-H:+ReportExceptionStackTraces",
  "--no-fallback",
  "--no-server",
  "--initialize-at-build-time",
  "--report-unsupported-elements-at-runtime",
  "--static"
)
