package object project {

  import lib._
  import cats.free._
  import shapeless._, ops.coproduct.Inject

  trait AppAlgebra extends ConsultationAlgebra with UserAlgebra {
    type App[A] = ConsultationOp[A] :+: UserOp[A] :+: CNil

    /** Automatically lifts any of the App's algebras into Free[App, A]
     */
    implicit def freeLift[A, G[_]](a: G[A])(implicit inj: Inject[App[A], G[A]]): Free[App, A] =
      Free.liftF[App, A](inj(a))

  }
  object AppAlgebra extends AppAlgebra

}
