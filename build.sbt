name := "logback-slack"

version := "1.0"

scalaVersion := "2.11.7"
organization := "com.yukimt"

libraryDependencies ++= Seq(
  "ch.qos.logback" %  "logback-classic" % "1.1.3",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.json4s" %% "json4s-native"   % "3.2.11"
)

publishTo := Some(Resolver.file("logback-slack",file("."))(Patterns(true, Resolver.mavenStyleBasePattern)))
