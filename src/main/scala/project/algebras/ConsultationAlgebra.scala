package project

trait ConsultationAlgebra {
  import models.{ ConsultationId, Consultation }

  sealed trait ConsultationOp[+Next]
  object consultation {
    case class Create(c: Consultation) extends ConsultationOp[Consultation]
    case class Get(c: ConsultationId) extends ConsultationOp[Consultation]
    case class GetAll() extends ConsultationOp[List[Consultation]]
    case class Validate(c: Consultation) extends ConsultationOp[Consultation]
  }

}
object ConsultationAlgebra extends ConsultationAlgebra

