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

package uk.gov.hmrc.snsclient.aws.sns

import javax.inject.{Inject, Named, Singleton}

import com.amazonaws.services.sns.model.{CreatePlatformEndpointResult, EndpointDisabledException}
import play.api.Logger
import uk.gov.hmrc.snsclient.aws.AwsAsyncSupport
import uk.gov.hmrc.snsclient.metrics.Metrics
import uk.gov.hmrc.snsclient.model._

import scala.collection.immutable
import scala.concurrent.Future._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}


@Singleton
class SnsService @Inject()(client: SnsClientScalaAdapter, @Named("arnsByOs") arnsByOs: immutable.Map[String, String], metrics: Metrics) extends SnsApi with AwsAsyncSupport {

  override def publish(notifications: Seq[Notification])(implicit ctx: ExecutionContext): Future[Seq[DeliveryStatus]] = {

    val publishRequests = notifications.map(n => (n, client.publish(n)))

    traverse(publishRequests) {
      case (request, result) => result.map {
        _ =>
          metrics.publishSuccess()
          DeliveryStatus.success(request.id)
      } recover {
        case ex: EndpointDisabledException =>
          Logger.error(s"Publish request [${request.id}] failed because [${ex.getMessage}]")
          metrics.endpointDisabledFailure()
          DeliveryStatus.disabled(request.id, ex.getMessage)
        case ex =>
          Logger.error(s"Publish request [${request.id}] failed because [${ex.getMessage}]")
          metrics.publishFailure()
          DeliveryStatus.failure(request.id, ex.getMessage)
      }
    }
  }

  override def createEndpoints(endpoints: Seq[Endpoint])(implicit ctx: ExecutionContext): Future[Seq[CreateEndpointStatus]] = {

    traverse(batchCreateEndpoints(endpoints)) {
      case (request, result) =>
        result.map { arn =>
          metrics.endpointCreationSuccess()
          CreateEndpointStatus.success(request.registrationToken, arn.getEndpointArn)
        } recover {
          case ex =>
            Logger.error("Endpoint creation failed", ex)
            metrics.endpointCreationFailure()
            CreateEndpointStatus.failure(request.registrationToken)
        }
    }
  }

  private def batchCreateEndpoints(endpoints: Seq[Endpoint])(implicit ctx: ExecutionContext): Seq[(Endpoint, Future[CreatePlatformEndpointResult])] = {
    endpoints.map {
      endpoint =>
        arnsByOs.get(endpoint.os) match {
          case Some(appName) => (endpoint, client.createEndpoint(endpoint.registrationToken, appName))
          case None =>
            Logger.error(s"Endpoint creation failed because the Native OS ${endpoint.os} is not configured to a PlatformApplication")
            metrics.unknownOsFailure()
            (endpoint, Future failed new IllegalArgumentException(s"No platform application can be found for the os[${endpoint.os}]"))
        }
    }
  }
}



