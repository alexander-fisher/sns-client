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

import javax.inject.{Inject, Singleton}

import com.amazonaws.services.sns.model.CreatePlatformEndpointResult
import play.api.Logger
import uk.gov.hmrc.snsclient.aws.AwsAsyncSupport
import uk.gov.hmrc.snsclient.model._

import scala.concurrent.Future._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}


@Singleton
class SnsService @Inject() (client: SnsClientScalaAdapter, configuration:SnsConfiguration) extends SnsApi with AwsAsyncSupport {

  private def platformApplicationName(os:String) : Option[String] =  {
    configuration.platformsApplicationsOsMap.get(os)
  }

  override def publish(notifications: Seq[Notification])(implicit ctx:ExecutionContext) = {

    val publishRequests = notifications.map(n => (n, client.publish(n)))

    traverse(publishRequests) {
      case (request, result) => result.map(_ => DeliveryStatus.success(request.id)) recover {
        case ex => DeliveryStatus.failure(request.id, ex.getMessage)
      }
    }
  }

  override def createEndpoint(endpoints: Seq[Endpoint])(implicit ctx:ExecutionContext) = {

    traverse(batchCreateEndpoints(endpoints)) {
      case (request, result) => result.map(arn => CreateEndpointStatus.success(request.registrationToken, arn.getEndpointArn)) recover {
        case ex =>
          Logger.warn("SNS Application Endpoint creation failed", ex)
          CreateEndpointStatus.failure(request.registrationToken)
      }
    }
  }

  private def batchCreateEndpoints(endpoints: Seq[Endpoint])(implicit ctx:ExecutionContext): Seq[(Endpoint, Future[CreatePlatformEndpointResult])] = {
    endpoints.map {
      endpoint =>
        platformApplicationName(endpoint.os) match {
          case Some(appName) => (endpoint, client.createEndpoint(endpoint, appName))
          case None => (endpoint, Future failed new IllegalArgumentException(s"No platform application can be found for the os[${endpoint.os}]"))
        }
    }
  }
}



