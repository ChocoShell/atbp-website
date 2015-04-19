name := "atbp-info"

version := "1.0.0"

scalaVersion := "2.11.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23"
)

