package project.lib

trait FreeMarshallerModule extends io.buildo.base.MonadicCtrlRouterModule
  with io.buildo.base.MonadicCtrlModule
  with project.CtrlFlowInterpreterModule
  with project.JsonSerializerModule {

  import scala.concurrent.Future
  import spray.http._
  import spray.httpx.marshalling._
  import project.AppAlgebra._

  import JsonProtocol._
  import spray.httpx.SprayJsonSupport._

  //FIXME: do something more parametric
  import ComposableInterpreterImplicits._
  val ctrlFlowInterpreters = CtrlFlowConsultationInterpreter or CtrlFlowUserInterpreter

  implicit def monadInstance(implicit
    ec: scala.concurrent.ExecutionContext
  ): cats.Monad[FutureCtrlFlow] = {
    import scalaz._; import Scalaz._
    new cats.Monad[FutureCtrlFlow] {
      def pure[A](a: A): FutureCtrlFlow[A] =
        scalaz.Monad[FutureCtrlFlow].point(a)

      def flatMap[A, B](fa: FutureCtrlFlow[A])(f: A => FutureCtrlFlow[B]): FutureCtrlFlow[B] =
        scalaz.Monad[FutureCtrlFlow].bind(fa)(f)
    }
  }

  implicit def freeMarshallerTMarshaller[A](implicit
    m: ToResponseMarshaller[FutureCtrlFlow[A]],
    ex: scala.concurrent.ExecutionContext
  ): ToResponseMarshaller[cats.free.Free[App, A]] =
    ToResponseMarshaller[cats.free.Free[App, A]] { (value, ctx) =>
      m(value.foldMap(ctrlFlowInterpreters), ctx)
    }

}
