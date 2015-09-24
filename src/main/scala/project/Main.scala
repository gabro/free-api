package project

import lib._
import lib.Implicits._

import cats._
import cats.free.Free

object Main extends scala.App {
  import Algebra._
  import models._

  def app = for {
    u <- user.Get("123")
    c = Consultation("123", "A consultation")
    _ <- consultation.Get(c._id)
  } yield ()

  def interpreters = ConsultationInterpreter or UserInterpreter
  app.foldMap(interpreters)
}

trait Algebra extends ConsultationAlgebra with UserAlgebra {
  type App[A] = Coproduct[ConsultationOp, UserOp, A]

  /** Automatically lifts any of the App's algebras into Free[App, A]
   */
  implicit def freeLift[A, G[_]](a: G[A])(implicit I: Inject[G, App]): Free[App, A] =
    Free.liftF[App, A](I.inj(a))
}
object Algebra extends Algebra

trait ConsultationAlgebra {
  import models.{ ConsultationId, Consultation }

  sealed trait ConsultationOp[A]
  object consultation {
    case class Create(c: Consultation) extends ConsultationOp[Unit]
    case class Get(s: ConsultationId) extends ConsultationOp[Option[Consultation]]
  }

}
object ConsultationAlgebra extends ConsultationAlgebra

trait UserAlgebra {
  import models.{ UserId, User }

  sealed trait UserOp[A]
  object user {
    case class Get(id: UserId) extends UserOp[Option[User]]
  }

}
object UserAlgebra extends UserAlgebra

// -------------------------------------------------------------------------------------------------
//              INTERPRETERS
// -------------------------------------------------------------------------------------------------

object ConsultationInterpreter extends (Algebra.ConsultationOp ~> Id) {
  import Algebra._
  import models._

  // the "db"
  var c: Option[Consultation] = None

  def apply[A](in: ConsultationOp[A]): Id[A] =
    in match {
      case consultation.Create(a) =>
        // storing it in the "db"
        c = Some(a)
        println(s"Creating consultation $a")
      case consultation.Get(id) =>
        println(s"Getting consultation with id: $id")
        val r = c.filter(_._id == id)
        r match {
          case Some(r) => println(s"Found consultation $r")
          case None => println("Consultation not found")
        }
        r
    }
}

object UserInterpreter extends (Algebra.UserOp ~> Id) {
  import Algebra._
  import models._

  // the "db"
  var u: Option[User] = Some(User(_id = "123", name = "Gabro"))

  def apply[A](in: UserOp[A]): Id[A] =
    in match {
      case user.Get(id) =>
        val r = u.filter(_._id == id)
        r match {
          case Some(r) => println(s"Found user $r")
          case None => println("User not found")
        }
        r
    }
}
