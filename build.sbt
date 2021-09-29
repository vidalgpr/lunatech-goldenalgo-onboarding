
lazy val commonSettings = Seq(
  scalaVersion := "2.13.6",
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation"
  )
)

lazy val client = (project in file("client"))
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js"                      %%% "scalajs-dom"   % "1.0.0",
      "io.suzaku"                         %%% "diode-core"    % "1.1.13",
      "io.suzaku"                         %%% "diode-react"   % "1.1.13",
      "io.circe"                          %%% "circe-core"    % "0.13.0",
      "io.circe"                          %%% "circe-generic" % "0.13.0",
      "io.circe"                          %%% "circe-parser"  % "0.13.0",
      "com.github.japgolly.scalajs-react" %%% "core"          % "1.7.7",
      "com.github.japgolly.scalajs-react" %%% "extra"         % "1.7.7"
    ),
    Compile / npmDependencies ++= Seq("react" -> "16.13.1", "react-dom" -> "16.13.1"),
    (fastOptJS / webpackBundlingMode) := BundlingMode.LibraryAndApplication(),
    Compile / fastOptJS / artifactPath := ((Compile / fastOptJS / crossTarget).value /
    ((fastOptJS / moduleName).value + "-opt.js"))
  )

lazy val server = (project in file("server"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"        % "10.2.4",
      "de.heikoseeberger" %% "akka-http-circe"  % "1.36.0",
      "com.typesafe.akka" %% "akka-stream"      % "2.6.15",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.15",
      "io.circe"          %% "circe-core"       % "0.13.0",
      "io.circe"          %% "circe-generic"    % "0.13.0",
      "io.circe"          %% "circe-parser"     % "0.13.0"
    )
  )

lazy val root = (project in file("."))
  .aggregate(server)
  .aggregate(client)
