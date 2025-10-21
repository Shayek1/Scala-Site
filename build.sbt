ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "SiteGenerator"
  )

lazy val sitegenerator = (project in file("sitegenerator"))
  .settings(
    name := "sitegenerator",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib"    % "0.11.4",
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.lihaoyi" %% "cask"      % "0.10.2",
      // markdown
      "org.commonmark" % "commonmark" % "0.24.0",
    )
  )