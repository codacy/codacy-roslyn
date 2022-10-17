name := "codacy-roslyn"

scalaVersion := "2.13.10"

enablePlugins(NativeImagePlugin)

//This setting avoids a empty image with java being created if anythings fails while nativeImage
nativeImageOptions += "--no-fallback"
