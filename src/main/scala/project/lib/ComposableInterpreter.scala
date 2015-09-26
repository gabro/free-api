package project.lib

import shapeless._

import cats.~>

class ComposableInterpreter[F[_], H[_]](f: F ~> H) {
  def or[G[_]](g: G ~> H): ({ type x[A] = F[A] :+: G[A] :+: CNil })#x ~> H = {
    new (({ type x[A] = F[A] :+: G[A] :+: CNil })#x ~> H) {
      def apply[A](fa: F[A] :+: G[A] :+: CNil): H[A] =
        (fa.select[F[A]], fa.select[G[A]]) match {
          case (Some(ff), None) => f(ff)
          case (None, Some(gg)) => g(gg)
          // this can't happen, due to the definition of Coproduct
          case _ => throw new Exception("Something is wrong, most likely in the type system")
        }
    }
  }
}

trait ComposableInterpreterImplicits {
  implicit def apply[F[_], H[_]](f: F ~> H) =
      new ComposableInterpreter(f)
}
object ComposableInterpreterImplicits extends ComposableInterpreterImplicits
