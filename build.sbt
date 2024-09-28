ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "recipe-lister"
  )

libraryDependencies ++= {

  Seq(
    "com.typesafe.akka" %% "akka-http" % "10.4.0",
    "com.typesafe.akka" %% "akka-stream" % "2.8.1",
    "io.circe" %% "circe-core" % "0.14.3",
    "io.circe" %% "circe-generic" % "0.14.3",
    "io.circe" %% "circe-parser" % "0.14.3",
    "com.typesafe.slick" %% "slick" % "3.5.1",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
  )
}

enablePlugins(JavaAppPackaging)
