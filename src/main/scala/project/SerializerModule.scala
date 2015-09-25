package project

trait JsonSerializerModule extends io.buildo.base.MonadicCtrlJsonModule with io.buildo.base.JsonModule {
  import spray.json._

  object JsonProtocol extends AutoProductFormat with DefaultJsonProtocol {
    import io.buildo.ingredients.jsend.JSendJsonProtocol._
    import io.buildo.ingredients.jsend.dsl._
    import scala.language.reflectiveCalls
    import models._

    implicit val ConsultationSI = `for`[Consultation] serializeOneAs ("consultation") andMultipleAs ("consultations")
    implicit val UserSI = `for`[User] serializeOneAs ("user") andMultipleAs ("users")

  }
}
