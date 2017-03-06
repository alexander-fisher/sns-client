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

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.Configuration
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.support.DefaultTestData

@RunWith(classOf[JUnitRunner])
class AwsSnsClientBuilderSpec extends UnitSpec with DefaultTestData {


  "AwsSnsClientBuilder" should {

    val configuration: SnsConfiguration = new SnsConfiguration(Configuration from Map(
      "aws.accessKey" -> "theAccessKey",
      "aws.secret" -> "theSecret",
      "aws.signingRegion" -> "theSigningRegion",
      "aws.serviceEndpoint" -> "theServiceEndpoint")
    )

    val builder = new AwsSnsClientBuilder(configuration)

    "populates AWS credentials from SnsConfiguration" in {
      val credentials = builder.credentials.getCredentials
      credentials.getAWSAccessKeyId shouldBe configuration.accessKey
      credentials.getAWSSecretKey shouldBe configuration.secret
    }

    "populates AWS endpoint from SnsConfiguration" in {
      val endpoint = builder.endpoint
      endpoint.getServiceEndpoint shouldBe configuration.serviceEndpoint
      endpoint.getSigningRegion shouldBe configuration.signingRegion
    }
  }
}
