scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions"
)

resolvers ++= Seq(
  "buildo" at "https://github.com/buildo/mvn/raw/master/releases",
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "org.spire-math" %% "cats" % "0.2.0",
  "io.buildo" %% "nozzle" % "0.5.0",
  "io.spray" %% "spray-json" % "1.3.1",
  "com.chuusai" %% "shapeless" % "2.2.5"
)

seq(Revolver.settings: _*)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.6.3")
