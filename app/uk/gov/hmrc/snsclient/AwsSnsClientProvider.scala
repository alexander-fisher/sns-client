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

package uk.gov.hmrc.snsclient

import javax.inject.{Inject, Singleton}

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sns.{AmazonSNSAsync, AmazonSNSAsyncClientBuilder}
import com.google.inject.{Provider, ProvisionException}
import play.api.Logger
import uk.gov.hmrc.snsclient.aws.sns.SnsConfiguration

@Singleton
class AwsSnsClientProvider @Inject()(snsConfig: SnsConfiguration) extends Provider[AmazonSNSAsync] {

  private def fail(error: String) = throw new ProvisionException(error)

  private def configureStubbedClient(builder: AmazonSNSAsyncClientBuilder, stub: String) = {
    Logger.info(s"sns-client started with stubbed endpoint[$stub]")
    builder withEndpointConfiguration new EndpointConfiguration(stub, "not-used-for-stubbing") build()
  }

  private def configureLiveClient(builder: AmazonSNSAsyncClientBuilder, reg: String) = {
    Logger.info(s"sns-client started with region[$reg]")
    builder.withRegion(reg).build()
  }

  override def get(): AmazonSNSAsync = {

    val builder = AmazonSNSAsyncClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = snsConfig.accessKey
        override def getAWSSecretKey: String = snsConfig.secret
      }))

    (snsConfig.region, snsConfig.stubbedEndpoint) match {
      case (Some(reg), None)                   => configureLiveClient(builder, reg)
      case (None, Some(stub)) if stub.nonEmpty => configureStubbedClient(builder, stub)
      case (None, None) => fail("One of either aws.region OR a [aws.regionOverrideForStubbing] must be provided")
      case (Some(reg), Some(stub)) => fail("Either [aws.region] or [aws.regionOverrideForStubbing] must be provided. Not both")
      case (None, Some(stub)) => fail(s"[aws.regionOverrideForStubbing] cannot be empty if using a stubbed endpoint. Either set [aws.region] or provide a value")
    }
  }
}
