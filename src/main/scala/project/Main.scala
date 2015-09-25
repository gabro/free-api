package project

import lib._
import lib.Implicits._

import cats._
import cats.free.Free
import cats.data._
import cats.std.future._
import cats.std.option._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._

object Main extends scala.App {
  import Algebra._
  import models._

  val app = for {
    c <- consultation.Create(Consultation("123", "A consultation"))
    d <- consultation.Get(c._id)
    _ <- consultation.Get("124") // this fails...
    _ <- consultation.Get("125") // ...so this won't be executed (monadic fail-fast semantic)
  } yield d

  val unsafeInterpreters = ConsultationInterpreter or UserInterpreter
  val safeInterpreters = OptionConsultationInterpreter or OptionUserInterpreter
  val asyncInterpreters = AsyncConsultationInterpreter or AsyncUserInterpreter

  // app.foldMap(unsafeInterpreters)

  app.foldMap(safeInterpreters)

  // val f = app.foldMap(asyncInterpreters)
  // // wait for the future to run (otherwise the main thread terminates)
  // Await.ready(f, 2.seconds)
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

  sealed trait ConsultationOp[+Next]
  object consultation {
    case class Create(c: Consultation) extends ConsultationOp[Consultation]
    case class Get(s: ConsultationId) extends ConsultationOp[Consultation]
  }

}
object ConsultationAlgebra extends ConsultationAlgebra

trait UserAlgebra {
  import models.{ UserId, User }

  sealed trait UserOp[+Next]
  object user {
    case class Get(id: UserId) extends UserOp[User]
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
        c.get
      case consultation.Get(id) =>
        println(s"Getting consultation with id: $id")
        val r = c.filter(_._id == id)
        r match {
          case Some(r) => println(s"Found consultation $r")
          case None => println("Consultation not found")
        }
        r.get
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
        r.get
    }
}

object OptionUserInterpreter extends (Algebra.UserOp ~> Option) {
  import Algebra._
  import models._

  // the "db"
  var u: Option[User] = Some(User(_id = "123", name = "Gabro"))

  def apply[A](in: UserOp[A]): Option[A] =
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

object OptionConsultationInterpreter extends (Algebra.ConsultationOp ~> Option) {
  import Algebra._
  import models._

  // the "db"
  var c: Option[Consultation] = None

  def apply[A](in: ConsultationOp[A]): Option[A] =
    in match {
      case consultation.Create(a) =>
        // storing it in the "db"
        c = Some(a)
        println(s"Creating consultation $a")
        c
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

object AsyncUserInterpreter extends (Algebra.UserOp ~> Future) {
  import Algebra._
  import models._

  // the "db"
  var u: Option[User] = Some(User(_id = "123", name = "Gabro"))

  def apply[A](in: UserOp[A]): Future[A] =
    in match {
      case user.Get(id) =>
        val r = u.filter(_._id == id)
        r match {
          case Some(r) => println(s"Found user $r")
          case None => println("User not found")
        }
        Future(r.get)
    }
}

object AsyncConsultationInterpreter extends (Algebra.ConsultationOp ~> Future) {
  import Algebra._
  import models._

  // the "db"
  var c: Option[Consultation] = None

  def apply[A](in: ConsultationOp[A]): Future[A] =
    in match {
      case consultation.Create(a) =>
        // storing it in the "db"
        c = Some(a)
        println(s"Creating consultation $a")
        Future(c.get)

      case consultation.Get(id) =>
        println(s"Getting consultation with id: $id")
        val r = c.filter(_._id == id)
        r match {
          case Some(r) => println(s"Found consultation $r")
          case None => println("Consultation not found")
        }
        Future(r.get)
    }
}
