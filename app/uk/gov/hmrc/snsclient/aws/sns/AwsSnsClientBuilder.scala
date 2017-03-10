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

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sns.{AmazonSNSAsync, AmazonSNSAsyncClientBuilder}
import play.api.Logger

import scala.language.postfixOps

@Singleton()
class AwsSnsClientBuilder @Inject()(configuration: SnsConfiguration, builder:AmazonSNSAsyncClientBuilder) {

  import configuration._

  def getInstance: AmazonSNSAsync = {

    val builderWithCreds = builder.withCredentials(
      new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = accessKey
        override def getAWSSecretKey: String = secret
      }))

    (region, stubbedEndpoint) match {
      case (Some(reg), None)  =>
        Logger.info(s"sns-client started with region[$reg]")
        builderWithCreds.withRegion(reg).build()

      case (None, Some(stub)) if stub.nonEmpty =>
        Logger.info(s"sns-client started with stubbed endpoint[$stub]")
        builderWithCreds withEndpointConfiguration new EndpointConfiguration(stub, "ssss") build()
      case _ => failBuilding(region, stubbedEndpoint)
    }
  }

  private def failBuilding(region: Option[String], stubbedEndpoint: Option[String]) = {

    def fail(error:String) = throw new IllegalStateException(error)

    (region, stubbedEndpoint) match {
      case (None, None)            => fail("One of either aws.region OR a [aws.regionOverrideForStubbing] must be provided")
      case (Some(reg), Some(stub)) => fail("Either [aws.region] or [aws.regionOverrideForStubbing] must be provided. Not both")
      case (None, Some(stub))      => fail(s"[aws.regionOverrideForStubbing] cannot be empty if using a stubbed endpoint. Either set [aws.region] or provide a value")
    }
  }
}