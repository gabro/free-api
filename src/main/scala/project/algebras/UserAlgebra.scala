package project

trait UserAlgebra {
  import models.{ UserId, User }

  sealed trait UserOp[+Next]
  object user {
    case class Get(id: UserId) extends UserOp[User]
  }

}
object UserAlgebra extends UserAlgebra

