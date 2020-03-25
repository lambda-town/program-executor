import sbtghpackages.TokenSource.Environment

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.3.2"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "Lambdacademy"

ThisBuild / githubUser := sys.env.getOrElse("GITHUB_USER", "REPLACE_ME")
ThisBuild / githubOwner := "lambdacademy-dev"
ThisBuild / githubTokenSource := Some(Environment("GITHUB_TOKEN"))
ThisBuild / githubRepository := "program-executor"

lazy val root = (project in file("."))
  .settings(
    name := "program-executor",
    githubOwner := "lambdacademy-dev",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "2.1.0"
    )
  )

