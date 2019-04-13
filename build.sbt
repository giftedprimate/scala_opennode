organization := "com.github.giftedprimate"

name := "scala_opennode"

version := "0.2"

scalaVersion := "2.12.8"

homepage := Some(url("https://github.com/giftedprimate/scala_opennode"))
scmInfo := Some(ScmInfo(url("https://github.com/giftedprimate/scala_opennode"), "git@github.com:giftedprimate/scala_opennode.git"))
developers := List(
  Developer(
    "giftedprimate",
    "Richard Porter",
    "Richard@mathbot.com",
    url("https://github.com/giftedprimate")
  )
)
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true

publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

lazy val playStuffVersion = "2.0.1"
lazy val akkaHttpVersion = "10.1.1"
lazy val akkaVersion = "2.5.11"
lazy val mongoDriverVersion = "2.4.2"
lazy val scalaTestVersion = "4.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ahc-ws-standalone"  % playStuffVersion,
  "com.typesafe.play" %% "play-ws-standalone-json" % playStuffVersion,
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.11.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.0" % Runtime,
  "org.mongodb.scala"      %% "mongo-scala-driver" % mongoDriverVersion,
  "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestVersion % "test"
)