package project

trait ConsultationAlgebra {
  import models.{ ConsultationId, Consultation, User }

  sealed trait ConsultationOp[+Next]
  object consultation {
    case class Create(c: Consultation, u: User) extends ConsultationOp[Consultation]
    case class Get(c: ConsultationId) extends ConsultationOp[Consultation]
    case class GetAll() extends ConsultationOp[List[Consultation]]
    case class Validate(c: Consultation) extends ConsultationOp[Consultation]
  }

}
object ConsultationAlgebra extends ConsultationAlgebra

