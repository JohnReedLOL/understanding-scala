name := "episode1"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "johnreed2 bintray" at "http://dl.bintray.com/content/johnreed2/maven"

libraryDependencies += "scala.trace" %% "scala-trace-debug" % "2.2.14"

libraryDependencies += "com.twitter" %% "util-collection" % "6.34.0"

resolvers += Resolver.sonatypeRepo("releases")

def macroDependencies(version: String) =
  Seq(
    "org.scala-lang" % "scala-reflect" % version % "provided",
    "org.scala-lang" % "scala-compiler" % version % "provided"
  ) ++
    (if (version startsWith "2.10.")
      Seq(compilerPlugin("org.scalamacros" % s"paradise" % "2.0.0" cross CrossVersion.full),
        "org.scalamacros" %% s"quasiquotes" % "2.0.0")
    else
      Seq())

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= macroDependencies(scalaVersion.value)

libraryDependencies ++= Seq( // for sized types.
  "com.chuusai" %% "shapeless" % "2.3.1"
)

// "-Xprint:typer"

/*
class Foo {
  val code: String = "codeString"

  def Bar(s: String) = {
    s.length == 1
  }
}
 */

scalacOptions ++= Seq("-Xprint:typer", "-unchecked", "-deprecation", "-feature", "-Xlint", "-Xfatal-warnings", "-Yinline-warnings", "-Ywarn-inaccessible", "-Ywarn-nullary-override", "-Ywarn-nullary-unit")