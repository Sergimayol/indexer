val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "indexer",
    version := "0.1.0",
    scalaVersion := scala3Version,
    libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.8.10.1"
  )

assembly / assemblyJarName := "indexer.jar"
