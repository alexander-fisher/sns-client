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
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.snsclient.config.ConfigKeys._
import uk.gov.hmrc.support.ConfigurationSupport

@RunWith(classOf[JUnitRunner])
class SnsConfigurationSpec extends UnitSpec with ConfigurationSupport {

  "SnsConfiguration" should {

    val config = Map (
      awsAccessKey -> "v1",
      awsSecretKey -> "v2",
      awsStubbingKey -> false
    )

    s"fail to load if AWS $awsAccessKey is not set" in {
      config.shouldFailWithoutKey(awsAccessKey)
      config.shouldFailIfKeyIsEmpty(awsAccessKey)
    }

    s"fail to load if AWS $awsSecretKey is not set" in {
      config.shouldFailWithoutKey(awsSecretKey)
      config.shouldFailIfKeyIsEmpty(awsSecretKey)
    }

    s"fail to load if AWS $awsStubbingKey is not set" in {
      config.shouldFailWithoutBooleanKey(awsStubbingKey)
    }

    val androidConfig = config ++ Map (
      gcmApiKey -> "api",
      gcmApplicaitonArnKey -> "arn",
      gcmOsKey -> "android",
      awsStubbingKey -> false
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

    "build a seq of defined platforms" in {
      val config = loadConfig(androidConfig)
      new SnsConfiguration(config).platforms shouldBe List(
        Some(GcmConfiguration(
          config.getString(gcmOsKey).get,
          config.getString(gcmApiKey).get,
          config.getString(gcmApplicaitonArnKey).get))
      )
    }
  }


  implicit def toHelper(config:Map[String, Any]): KeyHelper = new KeyHelper(config)

  class KeyHelper(config: Map[String, Any]) {
    def shouldFailWithoutKey(key:String)   = shouldFail(config - key)
    def shouldFailIfKeyIsEmpty(key:String) = shouldFail(config updated(key, ""))
    def shouldFailWithoutBooleanKey(key:String)   = shouldFail(config - key)
    private def shouldFail(properties:Map[String, Any]) = intercept[IllegalArgumentException](new SnsConfiguration(loadConfig(properties)))
  }
}
