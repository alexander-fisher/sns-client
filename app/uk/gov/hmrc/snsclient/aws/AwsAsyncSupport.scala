package uk.gov.hmrc.snsclient.aws

import java.util.concurrent.{Future => JFuture}

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.concurrent.{ExecutionContext, Promise}
import scala.util.{Failure, Success}

trait AwsAsyncSupport {

  def withAsyncHandler[Request <: AmazonWebServiceRequest, Result]
  (f: AsyncHandler[Request,Result] => JFuture[Result]) (implicit ctx:ExecutionContext): concurrent.Future[Result] = {

    val promise = Promise[Result]()
    promise.future.onComplete {
      case Success(r) => promise.success(r)
      case Failure(e) => promise.failure(e)
    }

    f(asyncHandler(promise))
    promise.future
  }

  def asyncHandler[Request <: AmazonWebServiceRequest, Result](promise:Promise[Result])  = {
    new AsyncHandler[Request, Result] {
      override def onError(exception: Exception): Unit = promise.failure(exception)
      override def onSuccess(request: Request, result: Result): Unit = promise.success(result)
    }
  }
}
