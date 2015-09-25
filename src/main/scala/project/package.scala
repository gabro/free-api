package object project {

  import lib._
  import cats.free._

  trait AppAlgebra extends ConsultationAlgebra with UserAlgebra {
    type App[A] = Coproduct[ConsultationOp, UserOp, A]

    /** Automatically lifts any of the App's algebras into Free[App, A]
     */
    implicit def freeLift[A, G[_]](a: G[A])(implicit I: Inject[G, App]): Free[App, A] =
      Free.liftF[App, A](I.inj(a))

  }
  object AppAlgebra extends AppAlgebra

}
