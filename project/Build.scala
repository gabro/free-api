import sbt._
import Keys._

object FreeApiBuild extends Build {
  lazy val root = Project(id = "free-api", base = file(".")) dependsOn(macroProj)

  lazy val macroProj = Project(id = "macro", base = file("macro"))
}
