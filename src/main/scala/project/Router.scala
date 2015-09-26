package project

trait RouterModule extends lib.FreeMarshallerModule
    with ConsultationControllerModule {

  import scala.concurrent.ExecutionContext.Implicits.global
  import spray.routing._

  class RouterActorImpl extends RouterActorImplBase with Router
  override def routerClass = classOf[RouterActorImpl]

  trait Router extends RouterBase {
    implicit val af = actorRefFactory

    import models._
    import JsonProtocol._
    import spray.httpx.SprayJsonSupport._

    val route = handleRejections(RejectionHandler.Default) {
      pathPrefix("consultations") {
        (get & path(Segment)) { id =>
          complete(consultationController.getById(id))
        } ~
        (get & pathEnd) {
          complete(consultationController.getAll)
        } ~
        (post & entity(as[Consultation])) { c =>
          val doctor = User("24", "Dr. Luca Cioria", UserRole.Doctor)
          val patient = User("42", "Gabriele Petronella", UserRole.Patient)
          val u = List(doctor, patient)(scala.util.Random.nextInt(2))
          complete(consultationController.create(c, u))
        }
      }
    }
  }
}

