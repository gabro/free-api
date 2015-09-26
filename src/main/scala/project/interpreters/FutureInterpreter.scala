package project

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import cats._

object AsyncUserInterpreter extends (AppAlgebra.UserOp ~> Future) {
  import AppAlgebra._
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

object AsyncConsultationInterpreter extends (AppAlgebra.ConsultationOp ~> Future) {
  import AppAlgebra._
  import models._

  // the "db"
  var c: Option[Consultation] = None

  def apply[A](in: ConsultationOp[A]): Future[A] =
    in match {
      case consultation.Create(a, _) =>
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
