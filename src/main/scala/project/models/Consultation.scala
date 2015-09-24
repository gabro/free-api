package project.models

case class Consultation(_id: ConsultationId, title: String, creator: Option[User] = None)
