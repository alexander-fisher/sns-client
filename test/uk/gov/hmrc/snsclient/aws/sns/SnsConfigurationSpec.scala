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
import uk.gov.hmrc.crypto.{CryptoGCMWithKeysFromConfig, PlainText}
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.snsclient.config.ConfigKeys._
import uk.gov.hmrc.snsclient.config.EncryptionHelper
import uk.gov.hmrc.support.ConfigurationSupport

@RunWith(classOf[JUnitRunner])
class SnsConfigurationSpec extends UnitSpec with ConfigurationSupport {

  val secretConfigMap = Map(s"$awsEncryptionKey.key" -> "gvBoGdgzqG1AarzF1LY0zQ==")

  trait TestScrambler extends EncryptionHelper {
    val secretConfig = loadConfig(secretConfigMap)
    val cipher = CryptoGCMWithKeysFromConfig(awsEncryptionKey, secretConfig)
    def scramble(s: String): String = cipher.encrypt(PlainText(s)).value
  }

  trait Setup extends TestScrambler {
    val config = secretConfigMap ++ Map (
      awsAccessKey -> scramble("access-key"),
      awsSecretKey -> scramble("secret-key"),
      awsStubbingKey -> false
    )

    val androidConfig = config ++ Map (
      gcmApiKey -> scramble("api-key"),
      gcmApplicaitonArnKey -> "arn",
      gcmOsKey -> "android",
      awsStubbingKey -> false
    )
  }

  "EncryptionHelper" should {
    s"decrypt values given a $awsEncryptionKey" in new TestScrambler {
      def configuration: Configuration = loadConfig(secretConfigMap)
      def helper = new EncryptionHelper { }

      val plainText = "Mr. SMIGGS was a gentleman, And he lived in London town;"

      helper.plainTextValue(scramble(plainText), configuration) shouldBe plainText
    }
  }

  "SnsConfiguration" should {
    
    s"fail to load if AWS $awsAccessKey is not set" in new Setup {
      config.shouldFailWithoutKey(awsAccessKey)
      config.shouldFailIfKeyIsEmpty(awsAccessKey)
    }

    s"fail to load if AWS $awsSecretKey is not set" in new Setup {
      config.shouldFailWithoutKey(awsSecretKey)
      config.shouldFailIfKeyIsEmpty(awsSecretKey)
    }

    s"fail to load if AWS $awsStubbingKey is not set" in new Setup {
      config.shouldFailWithoutBooleanKey(awsStubbingKey)
    }

    s"fail if $gcmOsKey is missing from the Android configuration" in new Setup {
      androidConfig.shouldFailWithoutKey(gcmOsKey)
      androidConfig.shouldFailIfKeyIsEmpty(gcmOsKey)
    }

    s"fail if $gcmApplicaitonArnKey is missing from the Android configuration" in new Setup {
      androidConfig.shouldFailWithoutKey(gcmApplicaitonArnKey)
      androidConfig.shouldFailIfKeyIsEmpty(gcmApplicaitonArnKey)
    }

    s"fail if $gcmApiKey is missing from the Android configuration" in new Setup {
      androidConfig.shouldFailWithoutKey(gcmApiKey)
      androidConfig.shouldFailIfKeyIsEmpty(gcmApiKey)
    }

    "build a seq of defined platforms" in new Setup {
      val theConfig: Configuration = loadConfig(androidConfig)
      new SnsConfiguration(theConfig).platforms shouldBe List(
        Some(GcmConfiguration(
          theConfig.getString(gcmOsKey).get,
          plainTextValue(theConfig.getString(gcmApiKey).get, secretConfig),
          theConfig.getString(gcmApplicaitonArnKey).get))
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
