import sbt._
import Keys._

object Common {

  val scalaSettings = Seq(scalaVersion := "2.12.7", scalacOptions += "-deprecation")

  val commonDepts = libraryDependencies ++= Seq("org.slf4j" % "slf4j-simple" % "1.7.25")

}
