import sbtghpackages.TokenSource.Environment

ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "lambda"
ThisBuild / organizationName := "Lambdacademy"
ThisBuild / fork := true
ThisBuild / githubOwner := "lambdacademy-dev"
ThisBuild / githubTokenSource := Some(Environment("GITHUB_TOKEN"))
ThisBuild / githubUser := sys.env.getOrElse("GITHUB_USER", "REPLACE_ME")

lazy val root = (project in file("."))
  .settings(
    name := "program-executor",
    libraryDependencies ++= Seq(
      "com.zaxxer" % "nuprocess" % "1.2.5",
      "co.fs2" %% "fs2-core" % "2.1.0"
    )
  )

