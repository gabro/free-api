package project.models

sealed trait UserRole extends CaseEnum
object UserRole {
  case object Patient extends UserRole
  case object Doctor extends UserRole
}

case class User(
  _id: UserId,
  name: String,
  role: UserRole = UserRole.Patient,
  active: Boolean = false
)
