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

    import CaseEnumMacro.caseEnumSerializer

    def caseEnumJsonFormat[T <: CaseEnum](implicit caseEnumSerializer: CaseEnumSerializer[T]) = new JsonFormat[T] {
      def write(value: T) = caseEnumSerializer.toString(value) match {
        case Some(str) => JsString(str)
        case None => JsNull
      }
      def read(jsvalue: JsValue) = jsvalue match {
        case JsString(str) => caseEnumSerializer.fromString(Some(str))
        case JsNull => caseEnumSerializer.fromString(None)
        case x => deserializationError(s"Expected JsString or JsNull, got $x")
      }
    }

    implicit val UserRoleFormat = caseEnumJsonFormat[UserRole]

  }
}
