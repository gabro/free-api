package project.lib

/** Inject type class as described in "Data types a la carte" (Swierstra 2008).
*/
sealed trait Inject[F[_],G[_]] {
  def inj[A](sub: F[A]): G[A]
  def prj[A](sup: G[A]): Option[F[A]]
}

object Inject {
  implicit def injRefl[F[_]] = new Inject[F,F] {
    def inj[A](sub: F[A]) = sub
    def prj[A](sup: F[A]) = Some(sup)
  }

  implicit def injLeft[F[_],G[_]] = new Inject[F,({type λ[α] = Coproduct[F,G,α]})#λ] {
    def inj[A](sub: F[A]) = Coproduct(Left(sub))
    def prj[A](sup: Coproduct[F,G,A]) = sup.run match {
      case Left(fa) => Some(fa)
      case Right(_) => None
    }
  }

  implicit def injRight[F[_],G[_],H[_]](implicit I: Inject[F,G]) =
    new Inject[F,({type f[x] = Coproduct[H,G,x]})#f] {
      def inj[A](sub: F[A]) = Coproduct(Right(I.inj(sub)))
      def prj[A](sup: Coproduct[H,G,A]) = sup.run match {
        case Left(_) => None
        case Right(x) => I.prj(x)
    }
  }
}
