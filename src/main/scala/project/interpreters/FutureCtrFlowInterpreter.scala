package project

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import cats._

trait CtrlFlowInterpreterModule extends io.buildo.base.MonadicCtrlModule {
  object CtrlFlowConsultationInterpreter extends (AppAlgebra.ConsultationOp ~> FutureCtrlFlow) {
    import AppAlgebra._
    import models._

    // the "db"
    var consultations = scala.collection.mutable.HashMap[ConsultationId, Consultation]()

    def apply[A](in: ConsultationOp[A]): FutureCtrlFlow[A] = {
      import scalaz._, Scalaz._

      in match {
        case consultation.Create(c, u) =>
          println(s"Creating consultation $c")
          // storing it in the "db"
          val c1 = c.copy(creator = u.pure[Option])
          consultations += c1._id -> c1
          Monad[FutureCtrlFlow].pure(c1)

        case consultation.Get(id) =>
          println(s"Getting consultation with id: $id")
          val c = consultations.get(id)
          c match {
            case Some(c) =>
              println(s"Found consultation $c")
              Monad[FutureCtrlFlow].pure(c)
            case None =>
              println("Consultation not found")
              EitherT.left(Future.successful(CtrlError.NotFound))
          }

        case consultation.GetAll() =>
          println(s"Getting all consultations")
          Monad[FutureCtrlFlow].pure(consultations.values.toList)

        case consultation.Validate(c) => {

          def checkConsultationId(c: Consultation): ValidationNel[CtrlError, Consultation] =
            if (c._id.length > 2) c.successNel else CtrlError.NotFound.failureNel

          def checkConsultationTitle(c: Consultation): ValidationNel[CtrlError, Consultation] =
            if (c.title.length > 2) c.successNel else CtrlError.NotFound.failureNel

          def checkConsultation(c: Consultation): ValidationNel[CtrlError, Consultation] =
            checkConsultationId(c) *> checkConsultationTitle(c)

          scalaz.EitherT {
            checkConsultation(c).disjunction.leftMap(_.head).point[Future]
          }
        }

      }
    }
  }

  object CtrlFlowUserInterpreter extends (AppAlgebra.UserOp ~> FutureCtrlFlow) {
    import AppAlgebra._
    import models._

    // the "db"
    var u: Option[User] = Some(User(_id = "123", name = "Gabro"))

    def apply[A](in: UserOp[A]): FutureCtrlFlow[A] = {
      import scalaz._, Scalaz._
      in match {
        case user.Get(id) =>
          val r = u.filter(_._id == id)
          r match {
            case Some(r) => println(s"Found user $r")
            case None => println("User not found")
          }
          Monad[FutureCtrlFlow].pure(r.get)

        case user.ValidateRole(u, role) =>
           if (u.role == role) {
             Monad[FutureCtrlFlow].pure(u)
           } else {
             EitherT.left(Future.successful(CtrlError.InvalidCredentials))
           }

        case user.EnsureActive(u) =>
          Monad[FutureCtrlFlow].pure(u.copy(active = true))
      }
    }
  }
}
