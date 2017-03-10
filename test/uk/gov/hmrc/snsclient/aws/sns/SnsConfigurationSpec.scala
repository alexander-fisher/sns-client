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
import uk.gov.hmrc.support.ConfigKeys.{gcmApplicaitonArnKey, _}

@RunWith(classOf[JUnitRunner])
class SnsConfigurationSpec extends UnitSpec {

  def loadConfig(properties: Map[String, String]): Configuration = Configuration from properties


  "SnsConfiguration" should {

    val config = Map (
      awsAccessKey -> "v1",
      awsSecretKey -> "v2"
    )

    s"fail to load if AWS $awsAccessKey is not set" in {
      config.shouldFailWithoutKey(awsAccessKey)
      config.shouldFailIfKeyIsEmpty(awsAccessKey)
    }

    s"fail to load if AWS $awsSecretKey is not set" in {
      config.shouldFailWithoutKey(awsSecretKey)
      config.shouldFailIfKeyIsEmpty(awsSecretKey)
    }


    val androidConfig = config ++ Map (
      gcmApiKey -> "api",
      gcmApplicaitonArnKey -> "arn",
      gcmOsKey -> "Android"
    )

    s"fail if $gcmOsKey is missing from the Android configuration" in {
      androidConfig.shouldFailWithoutKey(gcmOsKey)
      androidConfig.shouldFailIfKeyIsEmpty(gcmOsKey)
    }

    s"fail if $gcmApplicaitonArnKey is missing from the Android configuration" in {
      androidConfig.shouldFailWithoutKey(gcmApplicaitonArnKey)
      androidConfig.shouldFailIfKeyIsEmpty(gcmApplicaitonArnKey)
    }

    s"fail if $gcmApiKey is missing from the Android configuration" in {
      androidConfig.shouldFailWithoutKey(gcmApiKey)
      androidConfig.shouldFailIfKeyIsEmpty(gcmApiKey)
    }

    "build a map of applicationArns by OS name" in {
      new SnsConfiguration(loadConfig(androidConfig)).platformsApplicationsOsMap shouldBe Map("Android" -> "arn")
    }
  }



  implicit def toHelper(config:Map[String, String]): KeyHelper = new KeyHelper(config)

  class KeyHelper(config: Map[String, String]) {
    def shouldFailWithoutKey(key:String)   = shouldFail(config - key)
    def shouldFailIfKeyIsEmpty(key:String) = shouldFail(config updated(key, ""))
    private def shouldFail(properties:Map[String, String]) = intercept[IllegalArgumentException](new SnsConfiguration(loadConfig(properties)))
  }
}
