package project

trait UserAlgebra {
  import models.{ UserId, User, UserRole }

  sealed trait UserOp[+Next]
  object user {
    case class Get(id: UserId) extends UserOp[User]
    case class ValidateRole(u: User, r: UserRole) extends UserOp[User]
    case class EnsureActive(u: User) extends UserOp[User]
  }

}
object UserAlgebra extends UserAlgebra

