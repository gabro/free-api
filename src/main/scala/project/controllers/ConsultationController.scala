package project

trait ConsultationControllerModule extends io.buildo.base.MonadicCtrlModule {
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

    def create(c: Consultation) = for {
      c <- consultation.Create(c)
    } yield c
  }
}

