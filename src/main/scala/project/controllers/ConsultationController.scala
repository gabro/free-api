package project

trait ConsultationControllerModule {
  object consultationController {
    import AppAlgebra._
    import models._

    def getById(id: String) = for {
      c <- consultation.Get(id)
      _ <- consultation.Validate(c)
    } yield c

    def getAll = for {
      cc <- consultation.GetAll()
    } yield cc

    def create(c: Consultation, u: User) = for {
      _ <- user.ValidateRole(u, UserRole.Doctor)
      u <- user.EnsureActive(u)
      c <- consultation.Create(c, u)
    } yield c
  }
}

