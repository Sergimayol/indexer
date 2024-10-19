val scala3Version = "3.3.1"

val indexerVersion = "0.0.1-SNAPSHOT"

val AkkaVersion = "2.9.6"

lazy val root = project
  .in(file("."))
  .settings(
    name := "indexer",
    version := indexerVersion,
    scalaVersion := scala3Version,
    resolvers += "Akka library repository".at("https://repo.akka.io/maven"),
    libraryDependencies ++= Seq(
      "org.xerial" % "sqlite-jdbc" % "3.46.0.0",
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
      "com.typesafe.slick" %% "slick" % "3.5.0",
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
    )
  )

assembly / assemblyJarName := "indexer.jar"
