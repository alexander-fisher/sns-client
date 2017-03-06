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

@RunWith(classOf[JUnitRunner])
class SnsConfigurationSpec extends UnitSpec {

  private def loadConfig(properties: Map[String, String]) = Configuration from properties

  def defaultConfig = Map(
    "aws.accessKey" -> "v1",
    "aws.secret" -> "v2",
    "aws.signingRegion" -> "v3",
    "aws.serviceEndpoint" -> "v4")

  private def requiresTheKey(key: String) = {
    intercept[IllegalArgumentException] {
      new SnsConfiguration(loadConfig(defaultConfig - key))
    }.getMessage shouldBe s"property at [$key] was missing"
  }

  private def keyIsNotEmpty(key: String) = {
    intercept[IllegalArgumentException] {
      new SnsConfiguration(loadConfig(defaultConfig updated(key, "")))
    }.getMessage shouldBe s"property at [$key] was empty"
  }

  "SnsConfiguration" should {

    "reads config from application.conf" in {
      new SnsConfiguration(loadConfig(defaultConfig))
    }

    "requires an accessKey to be set" in {
      requiresTheKey("aws.accessKey")
      keyIsNotEmpty("aws.accessKey")
    }

    "requires an secret to be set" in {
      requiresTheKey("aws.secret")
      keyIsNotEmpty("aws.secret")
    }

    "requires an signingRegion to be set" in {
      requiresTheKey("aws.signingRegion")
      keyIsNotEmpty("aws.signingRegion")
    }

    "requires an serviceEndpoint to be set" in {
      requiresTheKey("aws.serviceEndpoint")
      keyIsNotEmpty("aws.serviceEndpoint")
    }
  }
}
