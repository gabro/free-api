package project.lib

/** A façade for io.buildo.base.MonadicCtrlModule that provides
 *  a cats.Monad instance using scalaz.Monad
 */
trait CatsMonadicCtrlModule extends io.buildo.base.MonadicCtrlModule {
  import scalaz._; import Scalaz._
  @scala.annotation.implicitNotFound("An instance of scala.concurrent.ExecutionContext is required")
  implicit def monadInstance(implicit executionContext: scala.concurrent.ExecutionContext) =
    new cats.Monad[FutureCtrlFlow] {
      def pure[A](a: A): FutureCtrlFlow[A] =
        scalaz.Monad[FutureCtrlFlow].point(a)

      def flatMap[A, B](fa: FutureCtrlFlow[A])(f: A => FutureCtrlFlow[B]): FutureCtrlFlow[B] =
        scalaz.Monad[FutureCtrlFlow].bind(fa)(f)
    }
}
object CatsMonadicCtrlModule extends CatsMonadicCtrlModule
