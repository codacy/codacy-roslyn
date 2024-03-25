name := "codacy-roslyn"

ThisBuild / scalaVersion := "2.13.13"

libraryDependencies ++= Seq(
  "com.codacy" %% "codacy-engine-scala-seed" % "6.1.2",
  "com.lihaoyi" %% "ujson" % "3.2.0",
  "com.codacy" %% "codacy-analysis-cli-model" % "7.9.8",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

enablePlugins(NativeImagePlugin)

nativeImageOptions ++= Seq(
  "-O1",
  "-H:+ReportExceptionStackTraces",
  "--no-fallback",
  "--no-server",
  "--report-unsupported-elements-at-runtime",
  "--static"
)

assembly / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.discard
  case x if x.endsWith("/module-info.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

val roslynVersion = "1.19.0"

libraryDependencies ++= Seq(
  "com.codacy" %% "codacy-engine-scala-seed" % "6.1.2",
  "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
  "com.lihaoyi" %% "ujson" % "3.2.0",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

lazy val `doc-generator` = project
  .settings(
    Compile / sourceGenerators += Def.task {
      val file = (Compile / sourceManaged).value / "codacy" / "roslyn" / "Versions.scala"
      IO.write(
        file,
        s"""package com.codacy.roslyn
                        |object Versions {
                        |  val roslynVersion: String = "$roslynVersion"
                        |}
                        |""".stripMargin
      )
      Seq(file)
    }.taskValue,
    libraryDependencies ++= Seq("com.github.pathikrit" %% "better-files" % "3.9.2", "com.lihaoyi" %% "ujson" % "3.2.0")
  )
