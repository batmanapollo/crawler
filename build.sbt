val Http4sVersion = "0.21.7"
val CirceVersion = "0.13.0"
val Specs2Version = "4.10.0"
val LogbackVersion = "1.2.3"
val JsoupVersion = "1.13.1"
val TapirVersion = "0.16.15"

lazy val root = (project in file("."))
  .settings(
    organization := "ru.gis",
    name := "simple-crawler",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    libraryDependencies ++= Seq(
      "org.http4s"                  %% "http4s-blaze-server"      % Http4sVersion,
      "org.http4s"                  %% "http4s-blaze-client"      % Http4sVersion,
      "org.http4s"                  %% "http4s-circe"             % Http4sVersion,
      "org.http4s"                  %% "http4s-dsl"               % Http4sVersion,
      "io.circe"                    %% "circe-generic"            % CirceVersion,
      "org.specs2"                  %% "specs2-core"              % Specs2Version % "test",
      "ch.qos.logback"              %  "logback-classic"          % LogbackVersion,
      "org.jsoup"                   %  "jsoup"                    % JsoupVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-core"               % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s"       % TapirVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)
