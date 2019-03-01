organization := "com.github.scala_opennode"

sonatypeProfileName := "com.github.scala_opennode"

name := "scala_opennode"

version := "0.1"

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

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)