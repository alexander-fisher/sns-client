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

import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.model.{CreatePlatformEndpointRequest, CreatePlatformEndpointResult, PublishRequest, PublishResult}
import uk.gov.hmrc.snsclient.aws.AwsAsyncSupport
import uk.gov.hmrc.snsclient.model._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future._
import scala.language.{implicitConversions, postfixOps}


@Singleton
class SnsServiceBuilder @Inject()(configuration: SnsConfiguration) extends SnsApi with AwsSnsClientBuilder with AwsAsyncSupport {

  private lazy val client: AmazonSNSAsync = snsClient(configuration)

  override def publish(notifications: Seq[Notification])(implicit ctx:ExecutionContext) = {
    traverse(notifications.map(notification => (notification, publish(notification)))) {
      case (nt, futureResult) => futureResult.map(_ => DeliveryStatus.success(nt.id)) recover {
        case e => DeliveryStatus.failure(nt.id)
      }
    }
  }

  private def publish(notification: Notification)(implicit ctx:ExecutionContext) =
    withAsyncHandler[PublishRequest, PublishResult] { handler =>

      val request = new PublishRequest()
        .withMessage(notification.message)
        .withTargetArn(notification.targetArn)

      client.publishAsync(request, handler)
    }

  override def createEndpoint(endpoints: Seq[Endpoint])(implicit ctx:ExecutionContext) = {
    traverse(endpoints.map(endpoint => (endpoint, createEndpoint(endpoint)))) {
      case (e, futureResult) => futureResult.map(arn => CreateEndpointStatus.success(e.id, arn.getEndpointArn)) recover {
        case ex => CreateEndpointStatus.failure(e.id)
      }
    }
  }

  private def createEndpoint(endpoint: Endpoint)(implicit ctx:ExecutionContext) =
    withAsyncHandler[CreatePlatformEndpointRequest, CreatePlatformEndpointResult] { handler =>

      val request = new CreatePlatformEndpointRequest()
        .withPlatformApplicationArn(endpoint.applicationArn)
        .withToken(endpoint.deviceToken)

      client.createPlatformEndpointAsync(request, handler)
    }
}



