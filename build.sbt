name := "codacy-roslyn"

ThisBuild / scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "com.codacy" %% "codacy-engine-scala-seed" % "6.0.1",
  "com.lihaoyi" %% "ujson" % "2.0.0",
  "com.codacy" %% "codacy-analysis-cli-model" % "7.6.6",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test
)

enablePlugins(NativeImagePlugin)

nativeImageOptions ++= Seq(
  "-O1",
  "-H:+ReportExceptionStackTraces",
  //This setting avoids a empty image with java being created if anythings fails while nativeImage
  "--no-fallback",
  "--no-server",
  "--report-unsupported-elements-at-runtime",
  "--static"
)
