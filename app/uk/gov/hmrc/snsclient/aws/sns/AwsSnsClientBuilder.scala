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

import com.amazonaws.{ClientConfiguration, Protocol}
import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.partitions.model.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.{AmazonSNSAsync, AmazonSNSAsyncClientBuilder}

@Singleton()
class AwsSnsClientBuilder @Inject()(configuration: SnsConfiguration) {

  def credentials: AWSStaticCredentialsProvider = {
    new AWSStaticCredentialsProvider(new AWSCredentials {
      override def getAWSAccessKeyId: String = configuration.accessKey
      override def getAWSSecretKey: String = configuration.secret
    })
  }

  def endpoint: EndpointConfiguration = {
    new EndpointConfiguration(configuration.serviceEndpoint, configuration.signingRegion)
  }

  private def getInstance(credentials:AWSCredentialsProvider, endpointConfig:EndpointConfiguration): AmazonSNSAsync = {
    AmazonSNSAsyncClientBuilder
      .standard
      .withCredentials(credentials)
      .withRegion(Regions.EU_WEST_1)
        .withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTP).withProxyHost("localhost").withProxyPort(8888))
//      .withEndpointConfiguration(endpointConfig)
      .build()
  }

  def getInstance: AmazonSNSAsync = {
    getInstance(credentials, endpoint)
  }
}
