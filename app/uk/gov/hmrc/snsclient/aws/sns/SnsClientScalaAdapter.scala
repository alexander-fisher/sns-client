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
import uk.gov.hmrc.snsclient.model.{Endpoint, Notification}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class SnsClientScalaAdapter @Inject() (builder: AwsSnsClientBuilder) extends AwsAsyncSupport {

  val client: AmazonSNSAsync = builder getInstance

  def publish(notification: Notification)(implicit ctx:ExecutionContext): Future[PublishResult] =
    withAsyncHandler[PublishRequest, PublishResult] { handler =>

      val request = new PublishRequest()
        .withMessage(notification.message)
        .withTargetArn(notification.endpointArn)

      client.publishAsync(request, handler)
    }


  def createEndpoint(endpoint: Endpoint, platformApplicationArn:String)(implicit ctx:ExecutionContext): Future[CreatePlatformEndpointResult] =
    withAsyncHandler[CreatePlatformEndpointRequest, CreatePlatformEndpointResult] { handler =>

      val request = new CreatePlatformEndpointRequest()
        .withPlatformApplicationArn(platformApplicationArn)
        .withToken(endpoint.registrationToken)

      client.createPlatformEndpointAsync(request, handler)
    }
}
