scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions"
)

libraryDependencies ++= Seq(
  "org.spire-math" %% "cats" % "0.2.0"
)

seq(Revolver.settings: _*)
