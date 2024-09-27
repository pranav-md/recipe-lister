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
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.postgresql" % "postgresql" % "42.4.2",

    "org.scalatest" %% "scalatest" % "3.3.0-SNAP3" % Test,
    "org.scalatestplus" %% "scalatestplus-mockito" % "1.0.0-M2" % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % "10.5.0-M1" % Test,
    "com.typesafe.akka" %% "akka-http-jackson" % "10.5.0-M1",
    "com.typesafe.akka" %% "akka-testkit" % "2.8.0-M1" % Test,
    "com.typesafe.akka" %% "akka-protobuf-v3" % "2.8.0-M1"
  )
}
