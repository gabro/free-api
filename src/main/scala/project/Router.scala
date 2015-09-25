package project

trait RouterModule extends io.buildo.base.MonadicCtrlRouterModule
    with JsonSerializerModule
    with ConsultationControllerModule {

  import spray.routing._

  class RouterActorImpl extends RouterActorImplBase with Router
  override def routerClass = classOf[RouterActorImpl]

  trait Router extends RouterBase {
    implicit val af = actorRefFactory

    import AppAlgebra._
    import models._
    import JsonProtocol._
    import spray.httpx.SprayJsonSupport._

    val ctrlFlowInterpreters = CtrlFlowConsultationInterpreter or CtrlFlowUserInterpreter

    // FIXME: write a meta-marshaller for this
    def completeWithFree[A: spray.httpx.marshalling.Marshaller](res: cats.free.Free[App, A]) = {
      import scala.concurrent.ExecutionContext.Implicits.global

      val f = res.foldMap(ctrlFlowInterpreters).run
      onSuccess(f) {
        case scalaz.\/-(c) => complete(c)
        case scalaz.-\/(_) => complete("BOOM")
      }
    }

    val route = handleRejections(RejectionHandler.Default) {
      pathPrefix("consultations") {
        (get & path(Segment)) { id =>
          completeWithFree(consultationController.getById(id))
        } ~
        (get & pathEnd) {
          completeWithFree(consultationController.getAll)
        } ~
        (post & entity(as[Consultation])) { c =>
          completeWithFree(consultationController.create(c))
        }
      }
    }
  }
}

