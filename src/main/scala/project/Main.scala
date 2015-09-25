package project

object Boot extends io.buildo.base.Boot with RouterModule {
  lazy val projectName = "free-api"
  val b = boot()
  lazy val actorSystem = b.system
}

