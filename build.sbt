name := "travel-crawler"
version := "1.0"
scalaVersion := "2.11.8"

val AKKA_VERSION          = "2.4.14"
val AKKA_HTTP_VERSION     = "10.0.9"
val SCALA_LOGGING_VERSION = "3.5.0"
val LOGBACK_VERSION       = "1.1.7"
val DISPATCH_VERSION      = "0.11.2"
val JSON4S_VERSION        = "3.5.0"
val JODA_VERSION          = "2.9.4"
val SPECS_VERSION         = "3.9.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka"           %%  "akka-actor"            % AKKA_VERSION,
  "com.typesafe.akka"           %%  "akka-http"             % AKKA_HTTP_VERSION,
  "com.typesafe.akka"           %%  "akka-http-spray-json"  % AKKA_HTTP_VERSION,
  "ch.megard"                   %%  "akka-http-cors"        % "0.2.1",
  "com.typesafe.scala-logging"  %%  "scala-logging"         % SCALA_LOGGING_VERSION,
  "ch.qos.logback"              %  "logback-classic"        % LOGBACK_VERSION,
  "net.databinder.dispatch"     %%  "dispatch-core"         % DISPATCH_VERSION,
  "org.json4s"                  %% "json4s-native"          % "3.5.0",
  "org.json4s"                  %% "json4s-jackson"         % JSON4S_VERSION,
  "joda-time"                   %  "joda-time"              % JODA_VERSION,
  "org.jodd"                    % "jodd-lagarto"            % "3.8.0",
  "org.specs2"                  %%  "specs2-core"           % SPECS_VERSION % "test"
)