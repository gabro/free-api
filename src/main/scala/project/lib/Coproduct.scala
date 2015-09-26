package project.lib

import cats.~>

case class Coproduct[F[_],G[_],A](run: Either[F[A],G[A]])

class ComposableInterpreter[F[_], H[_]](f: F ~> H) {
  def or[G[_]](g: G ~> H): ({ type f[x] = Coproduct[F, G, x]})#f ~> H =
    new (({ type f[x] = Coproduct[F, G, x]})#f ~> H) {
      def apply[A](fa: Coproduct[F,G,A]): H[A] = fa.run match {
        case Left(ff)   => f(ff)
        case Right(gg)  => g(gg)
      }
  }
}

trait ComposableInterpreterImplicits {
  implicit def apply[F[_], H[_]](f: F ~> H) =
      new ComposableInterpreter(f)
}
object ComposableInterpreterImplicits extends ComposableInterpreterImplicits
