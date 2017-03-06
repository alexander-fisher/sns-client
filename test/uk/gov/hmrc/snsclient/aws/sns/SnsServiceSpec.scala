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

import com.amazonaws.services.sns.model.{CreatePlatformEndpointResult, PublishResult}
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import play.api.Configuration
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.snsclient.model.{CreateEndpointStatus, DeliveryStatus}
import uk.gov.hmrc.support.{DefaultTestData, ResettingMockitoSugar}

import scala.concurrent.{ExecutionContext, Future}

@RunWith(classOf[JUnitRunner])
class SnsServiceSpec extends UnitSpec with ResettingMockitoSugar with DefaultTestData with ScalaFutures {

  implicit val ctx: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
  private val client = resettingMock[SnsClientScalaAdapter]

  "SnsServiceSpec" should {

    "return a DeliveryStatus(\"SUCCESS\") when SNS client publishes successfully" in {

      when(client.publish(defaultNotification))
        .thenReturn(Future successful new PublishResult().withMessageId("message-id"))

      val service = new SnsService(client, defaultSnsConfiguration)
      val result = await(service.publish(Seq(defaultNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.success("GUID")
    }

    "return a DeliveryStatus(\"FAILURE\") when SNS client fails" in {

      when(client.publish(defaultNotification))
        .thenReturn(Future failed new RuntimeException("oh noes!"))

      val service = new SnsService(client, defaultSnsConfiguration)
      val result = await(service.publish(Seq(defaultNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.failure("GUID", "oh noes!")

    }


    "return a CreateEndpointStatus(\"SUCCESS\") when the SNS client creates the application endpoint" in {

      val configuration = Configuration from defaultConfig
        .updated("aws.platform.gcm.osName", "Android")
        .updated("aws.platform.gcm.applicationName", "gcmApplicationName")


      val endpointResult = new CreatePlatformEndpointResult()
      endpointResult.setEndpointArn("endpoint-arn")

      when(client.createEndpoint(defaultEndpoint, "gcmApplicationName")).thenReturn(Future successful endpointResult)

      val service = new SnsService(client, new SnsConfiguration(configuration))
      val result = await(service.createEndpoint(Seq(defaultEndpoint)))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.success(defaultEndpoint.deviceId, endpointResult.getEndpointArn)
    }

    "return a CreateEndpointStatus(\"FAILURE\") when the SNS client fails" in {

      val configuration = Configuration from defaultConfig
        .updated("aws.platform.gcm.osName", "Android")
        .updated("aws.platform.gcm.applicationName", "gcmApplicationName")


      when(client.createEndpoint(defaultEndpoint, "gcmApplicationName"))
        .thenReturn(Future failed new RuntimeException("oh noes!"))

      val service = new SnsService(client, new SnsConfiguration(configuration))
      val result = await(service.createEndpoint(Seq(defaultEndpoint)))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.failure(defaultEndpoint.deviceId, "oh noes!")
    }


    "return a CreateEndpointStatus(\"FAILURE\") when the endpoint contains an unknown OS" in {
      val service = new SnsService(client, defaultSnsConfiguration)
      val result = await(service.createEndpoint(Seq(defaultEndpoint.copy(os = "Baidu"))))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.failure(defaultEndpoint.deviceId, "No platform application can be found for the os[Baidu]")
    }
  }
}
