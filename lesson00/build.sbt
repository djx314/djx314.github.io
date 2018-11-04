Common.scalaSettings
Common.commonDepts

val circeVersion = "0.10.1"

libraryDependencies ++= Seq(
    "io.circe" %% "circe-core"
  , "io.circe" %% "circe-generic"
  , "io.circe" %% "circe-parser"
).map(_ % circeVersion)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

libraryDependencies += "com.geirsson" %% "scalafmt-core" % "1.6.0-RC4"
