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
import uk.gov.hmrc.snsclient.aws.AwsProxy
import uk.gov.hmrc.snsclient.config.ConfigKeys.{apiKey, applicationArnKey, _}
import uk.gov.hmrc.snsclient.model.NativeOS.{Android, Ios}
import uk.gov.hmrc.support.ConfigurationSupport

@RunWith(classOf[JUnitRunner])
class SnsConfigurationSpec extends UnitSpec with ConfigurationSupport {

  "SnsConfiguration" should {

    val config = Map(
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

    val androidKey = Android
    val iosKey = Ios
    val androidApplicationArnKey = applicationArnKey.replace("PLATFORM", androidKey)
    val iosApplicationArnKey = applicationArnKey.replace("PLATFORM", iosKey)
    val androidApiKey = apiKey.replace("PLATFORM", androidKey)
    val iosApiKey = apiKey.replace("PLATFORM", iosKey)

    val androidConfig = config ++ Map(
      awsStubbingKey -> false,
      androidApiKey -> "android_api",
      androidApplicationArnKey -> "android_arn",
      iosApiKey -> "ios_api",
      iosApplicationArnKey -> "ios_arn"
    )

    s"fail if $applicationArnKey is missing from a platform configuration" in {
      androidConfig.shouldFailWithoutKey(androidApplicationArnKey)
      androidConfig.shouldFailIfKeyIsEmpty(androidApplicationArnKey)
    }

    s"fail if $apiKey is missing from a platform configuration" in {
      androidConfig.shouldFailWithoutKey(androidApiKey)
      androidConfig.shouldFailIfKeyIsEmpty(androidApiKey)
    }

    "build a seq of defined platforms" in {
      val config = loadConfig(androidConfig)
      new SnsConfiguration(config).platforms shouldBe List(
        Some(GcmConfiguration(
          androidKey,
          config.getString(androidApiKey).get,
          config.getString(androidApplicationArnKey).get)),
        Some(GcmConfiguration(
          iosKey,
          config.getString(iosApiKey).get,
          config.getString(iosApplicationArnKey).get))
      )
    }

    val proxyConfig = config ++ Map(
      "proxy.username" -> "username",
      "proxy.password" -> "secret",
      "proxy.host" -> "outbound-proxy",
      "proxy.port" -> 1234
    )

    "build a proxy given a proxy configuration" in {
      val config = loadConfig(proxyConfig)
      new SnsConfiguration(config).awsProxy shouldBe Some(
        AwsProxy(
          config.getString("proxy.username").get,
          config.getString("proxy.password").get,
          config.getString("proxy.host").get,
          config.getInt("proxy.port").get
        )
      )
    }

    val proxyKeys = Seq(proxyUserNameKey, proxyPasswordKey, proxyHostKey, proxyPortKey)

    proxyKeys.foreach(missingKey =>
      s"fail to build a proxy if $proxyKey exists but $missingKey is missing" in {
        val partialConfig = proxyConfig - s"$proxyKey.$missingKey"

        intercept[IllegalArgumentException] {
          val config = loadConfig(partialConfig)
          new SnsConfiguration(config).awsProxy
        }.getMessage should include(s"property at [$missingKey] was missing")
      }
    )

    "not build a proxy when no proxy configuration is provided" in {
      val noProxyConfig = loadConfig(config)
      new SnsConfiguration(noProxyConfig).awsProxy shouldBe None
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
