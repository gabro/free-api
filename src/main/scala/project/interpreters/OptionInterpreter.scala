package project

import cats._

object OptionUserInterpreter extends (AppAlgebra.UserOp ~> Option) {
  import AppAlgebra._
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

object OptionConsultationInterpreter extends (AppAlgebra.ConsultationOp ~> Option) {
  import AppAlgebra._
  import models._

  // the "db"
  var c: Option[Consultation] = None

  def apply[A](in: ConsultationOp[A]): Option[A] =
    in match {
      case consultation.Create(a, _) =>
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

