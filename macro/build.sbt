organization  := "io.buildo"

version       := "0.1-SNAPSHOT"

scalaVersion  := "2.11.7"

scalacOptions := Seq("-unchecked",
                     "-deprecation",
                     "-feature",
                     "-encoding", "utf8",
                     "-feature",
                     "-language:implicitConversions",
                     "-language:postfixOps",
                     "-language:reflectiveCalls")


libraryDependencies ++= Seq(
  "io.spray"            %%  "spray-json"      % "1.2.6",
  "org.scala-lang"      %   "scala-reflect"   % "2.11.0"
)

