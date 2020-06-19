ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.4.0"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "Lambda Town"

githubOwner := "lambda-town"
resolvers += Resolver.githubPackages("lambda-town")
githubRepository := "api"
githubTokenSource :=  TokenSource.GitConfig("github.token") || TokenSource.Environment("GITHUB_TOKEN")

lazy val root = (project in file("."))
  .settings(
    name := "program-executor",
    githubOwner := "lambda-town",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "2.3.0",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    )
  )

