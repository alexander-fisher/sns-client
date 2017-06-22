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

import com.amazonaws.{ClientConfiguration, ClientConfigurationFactory}
import com.google.inject.ProvisionException
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.scalatest.junit.JUnitRunner
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.snsclient.aws.sns.SnsConfiguration
import uk.gov.hmrc.snsclient.config.ConfigKeys._
import uk.gov.hmrc.support.{ConfigurationSupport, DefaultTestData, ResettingMockitoSugar}

@RunWith(classOf[JUnitRunner])
class AwsSnsClientProviderSpec extends UnitSpec with ConfigurationSupport with DefaultTestData with ResettingMockitoSugar {

  "AwsSnsClientProvider" should {
    s"fail to build if the $awsStubbingKey is 'true' but the $awsRegionOverrideKey is not set" in {

      val config = defaultConfig.updated(awsStubbingKey, true) - awsRegionOverrideKey

      intercept[ProvisionException] {
        buildClient(config)
      }.getMessage should include("[aws.stubbing] was empty")
    }

    s"fail to build if the $awsStubbingKey is 'false' but the $awsRegionKey is not set" in {

      val config = defaultConfig.updated(awsStubbingKey, false) - awsRegionKey

      intercept[ProvisionException] {
        buildClient(config)
      }.getMessage should include("[aws.region] was empty")
    }

    s"build using a client proxy if all $proxyKey details are provided" in {
      val mockFactory = resettingMock[ClientConfigurationFactory]
      val clientConfiguration = new ClientConfiguration

      Mockito.when(mockFactory.getConfig).thenReturn(clientConfiguration)

      buildClient(withProxyConfig, mockFactory)

      clientConfiguration.getProxyUsername shouldBe withProxyConfig(s"$proxyKey.$proxyUserNameKey")
      clientConfiguration.getProxyPassword shouldBe withProxyConfig(s"$proxyKey.$proxyPasswordKey")
      clientConfiguration.getProxyHost shouldBe withProxyConfig(s"$proxyKey.$proxyHostKey")
      clientConfiguration.getProxyPort shouldBe withProxyConfig(s"$proxyKey.$proxyPortKey")
    }

    s"build without a client proxy if $proxyKey is not provided" in {
      val mockFactory = resettingMock[ClientConfigurationFactory]
      val clientConfiguration = new ClientConfiguration

      Mockito.when(mockFactory.getConfig).thenReturn(clientConfiguration)

      buildClient(defaultConfig, mockFactory)

      clientConfiguration.getProxyUsername shouldBe null
      clientConfiguration.getProxyPassword shouldBe null
      clientConfiguration.getProxyHost shouldBe null
      clientConfiguration.getProxyPort shouldBe -1
    }
  }

  private def buildClient(config: Map[String, Any], configFactory: ClientConfigurationFactory = new ClientConfigurationFactory) = {
    new AwsSnsClientProvider(new SnsConfiguration(loadConfig(config)), configFactory) get()
  }
}
