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
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.snsclient.model.{CreateEndpointStatus, DeliveryStatus, Endpoint}
import uk.gov.hmrc.support.{DefaultTestData, ResettingMockitoSugar}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future._

@RunWith(classOf[JUnitRunner])
class SnsServiceSpec extends UnitSpec with ResettingMockitoSugar with DefaultTestData with ScalaFutures {

  implicit val ctx: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val client = resettingMock[SnsClientScalaAdapter]
  val applicationArn = "arn:1234567890:foo"

  "SnsServiceSpec" should {

    val defaultSnsConfiguration = Map("Android" -> "applicationArn")

    "return a DeliveryStatus(\"Success\") when SNS client publishes successfully" in {

      when(client.publish(androidNotification)).thenReturn(successful(new PublishResult()))

      val service = new SnsService(client, defaultSnsConfiguration)
      val result = await(service.publish(Seq(androidNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.success(androidNotification.id)
    }

    "return a DeliveryStatus(\"Failed\") when SNS client fails" in {

      when(client.publish(androidNotification)).thenReturn(failed(new RuntimeException("oh noes!")))

      val service = new SnsService(client, defaultSnsConfiguration)
      val result = await(service.publish(Seq(androidNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.failure(androidNotification.id, "oh noes!")

    }

    "return a CreateEndpointStatus(\"Success\") when the SNS client creates the application endpoint" in {

      val endpointResult = new CreatePlatformEndpointResult()
      endpointResult.setEndpointArn("endpoint-arn")

      when(client.createEndpoint(androidEndpoint.registrationToken, applicationArn)).thenReturn(successful(endpointResult))

      val service = new SnsService(client, Map("Android" -> applicationArn))
      val result = await(service.createEndpoint(Seq(androidEndpoint)))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.success(androidEndpoint.registrationToken, endpointResult.getEndpointArn)
    }

    "return a CreateEndpointStatus(\"Failed\") when the SNS client fails" in {

      when(client.createEndpoint(androidEndpoint.registrationToken, applicationArn)).thenReturn(failed(new RuntimeException("oh noes!")))

      val service = new SnsService(client, Map.empty[String, String])
      val result = await(service.createEndpoint(Seq(androidEndpoint)))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.failure(androidEndpoint.registrationToken, "oh noes!")
    }

    "return a CreateEndpointStatus(\"Failed\") when the endpoint contains an unknown OS" in {
      val service = new SnsService(client, Map("Android" -> applicationArn))
      val result = await(service.createEndpoint(Seq(Endpoint("Baidu", "deviceToken"))))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.failure("deviceToken", "No platform application can be found for the os[Baidu]")
    }
  }
}
