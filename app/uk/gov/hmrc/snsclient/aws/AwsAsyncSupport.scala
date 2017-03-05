/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.snsclient.aws

import java.util.concurrent.{Future => JFuture}

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.concurrent.{ExecutionContext, Promise}

trait AwsAsyncSupport {

  @inline
  def withAsyncHandler[Request <: AmazonWebServiceRequest, Result]
  (f: AsyncHandler[Request,Result] => JFuture[Result]) (implicit ctx:ExecutionContext): concurrent.Future[Result] = {
    val promise = Promise[Result]()
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
