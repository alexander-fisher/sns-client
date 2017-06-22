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

import com.amazonaws.ClientConfigurationFactory
import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sns.{AmazonSNSAsync, AmazonSNSAsyncClientBuilder}
import com.google.inject.{Provider, ProvisionException}
import play.api.Logger
import uk.gov.hmrc.snsclient.aws.sns.SnsConfiguration

import scala.language.postfixOps

@Singleton
class AwsSnsClientProvider @Inject()(snsConfig: SnsConfiguration, clientConfigurationFactory: ClientConfigurationFactory) extends Provider[AmazonSNSAsync] {

  import snsConfig._
  import uk.gov.hmrc.snsclient.config.ConfigKeys._

  private def fail(error: String) = throw new ProvisionException(error)

  private def withNonEmptyValue[T, O](key: String, optionalValue: Option[T])(f: T => O): O =
    optionalValue map f getOrElse fail(s"[$key] was empty")

  override def get(): AmazonSNSAsync = {

    val clientConfig = clientConfigurationFactory getConfig

    snsConfig.awsProxy.foreach { proxy =>
      clientConfig.setProxyUsername(proxy.username)
      clientConfig.setProxyPassword(proxy.password)
      clientConfig.setProxyHost(proxy.host)
      clientConfig.setProxyPort(proxy.port)
    }

    val builderWithCreds = AmazonSNSAsyncClientBuilder.standard()
      .withClientConfiguration(clientConfig)
      .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = snsConfig.accessKey
        override def getAWSSecretKey: String = snsConfig.secret
      }))

    if (useStubbing) {
      withNonEmptyValue(awsStubbingKey, regionOverride) { v =>
        Logger.info(s"sns-client started with stubbed endpoint[$v]")
        builderWithCreds withEndpointConfiguration new EndpointConfiguration(v, "not-used-for-stubbing") build()
      }
    }
    else {
      withNonEmptyValue(awsRegionKey, region) {  v =>
        Logger.info(s"sns-client started with region[$v]")
        builderWithCreds.withRegion(v).build()
      }
    }
  }
}
