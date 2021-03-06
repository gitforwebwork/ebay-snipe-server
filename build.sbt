import scalariform.formatter.preferences._

name := "ebay-snipe-server"
organization := "net.ruippeixotog"
version := "0.2-SNAPSHOT"

scalaVersion := "2.12.2"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"))

libraryDependencies ++= Seq(
  "com.github.nscala-time"     %% "nscala-time"                % "2.16.0",
  "com.typesafe"                % "config"                     % "1.3.1",
  "com.typesafe.akka"          %% "akka-actor"                 % "2.4.18",
  "com.typesafe.akka"          %% "akka-http"                  % "10.0.6",
  "com.typesafe.akka"          %% "akka-http-spray-json"       % "10.0.6",
  "com.typesafe.akka"          %% "akka-slf4j"                 % "2.4.18",
  "io.spray"                   %% "spray-json"                 % "1.3.3",
  "net.ruippeixotog"           %% "scala-scraper"              % "2.0.0-RC2",
  "net.ruippeixotog"           %% "scala-scraper-config"       % "2.0.0-RC2",
  "ch.qos.logback"              % "logback-classic"            % "1.2.3"            % "runtime")

scalariformPreferences := scalariformPreferences.value
  .setPreference(DanglingCloseParenthesis, Prevent)
  .setPreference(DoubleIndentClassDeclaration, true)

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:implicitConversions")

// -- general packaging settings --

enablePlugins(JavaServerAppPackaging)

mainClass in Compile := Some("net.ruippeixotog.ebaysniper.SnipeServer")

sources in (Compile, doc) := Nil

// the resources to provide in the conf folder instead of inside the JAR file
val confResources = Seq("logback.xml")

// the resources to ignore when packaging
val excludedResources = Seq("application.conf")

// copy the confResources to the conf folder...
mappings in Universal := {
  confResources.flatMap { resName =>
    val resFile = (resourceDirectory in Compile).value / resName
    if(resFile.exists) Some(resFile -> ("conf/" + resName)) else None
  }
}

// ...and do not include them inside the JAR
mappings in (Compile, packageBin) ~= { _.filterNot {
  case (_, resName) => confResources.contains(resName) || excludedResources.contains(resName)
}}

// include the conf folder in the classpath when the start script is executed
scriptClasspath += "../conf"

// -- Docker packaging settings --

maintainer in Docker := "Rui Gonçalves <ruippeixotog@gmail.com>"

daemonUser in Docker := "root" // the server must be able to write to mounted volumes

dockerExposedPorts in Docker := Seq(3647)

dockerExposedVolumes in Docker := Seq("/opt/docker/appdata", "/opt/docker/logs")

dockerRepository := Some("ruippeixotog")
