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

import com.amazonaws.services.sns.model.{CreatePlatformEndpointResult, EndpointDisabledException, PublishResult}
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.snsclient.metrics.Metrics
import uk.gov.hmrc.snsclient.model.NativeOS._
import uk.gov.hmrc.snsclient.model.{CreateEndpointStatus, DeliveryStatus, Endpoint}
import uk.gov.hmrc.support.{DefaultTestData, ResettingMockitoSugar}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future._

@RunWith(classOf[JUnitRunner])
class SnsServiceSpec extends UnitSpec with ResettingMockitoSugar with DefaultTestData with ScalaFutures {

  implicit val ctx: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext

  private val metrics = resettingMock[Metrics]
  private val client = resettingMock[SnsClientScalaAdapter]
  private val applicationArn = "arn:1234567890:foo"

  private def newService(configuration:Map[String, String]) = {
    new SnsService(client, configuration, metrics)
  }

  "SnsServiceSpec" should {

    val defaultSnsPropertyMap = Map(Android -> "applicationArn")

    "return a DeliveryStatus(\"Success\") when SNS client publishes successfully" in {

      when(client.publish(androidNotification)).thenReturn(successful(new PublishResult()))

      val service = newService(defaultSnsPropertyMap)
      val result = await(service.publish(Seq(androidNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.success(androidNotification.id)
      verify(metrics, times(1)).publishSuccess()
      verifyNoMoreInteractions(metrics)
    }

    "return a DeliveryStatus(\"Failed\") when SNS client fails" in {

      when(client.publish(androidNotification)).thenReturn(failed(new RuntimeException("oh noes!")))

      val service = newService(defaultSnsPropertyMap)
      val result = await(service.publish(Seq(androidNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.failure(androidNotification.id, "oh noes!")
      verify(metrics, times(1)).publishFailure()
      verifyNoMoreInteractions(metrics)
    }

    "return a DeliveryStatus(\"Disabled\") when the endpointARN has been diabled" in {

      when(client.publish(androidNotification)).thenReturn(failed(new EndpointDisabledException("Endpoint is disabled")))

      val service = newService(defaultSnsPropertyMap)
      val result = await(service.publish(Seq(androidNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.disabled(androidNotification.id, "oh noes!")
      verify(metrics, times(1)).endpointDisabledFailure()
      verifyNoMoreInteractions(metrics)
    }

    "return a CreateEndpointStatus(\"Success\") when the SNS client creates the application endpoint" in {

      val endpointResult = new CreatePlatformEndpointResult()
      endpointResult.setEndpointArn("endpoint-arn")

      when(client.createEndpoint(androidEndpoint.registrationToken, applicationArn)).thenReturn(successful(endpointResult))

      val service = newService( Map("android" -> applicationArn))
      val result = await(service.createEndpoints(Seq(androidEndpoint)))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.success(androidEndpoint.registrationToken, endpointResult.getEndpointArn)
      verify(metrics, times(1)).endpointCreationSuccess()
      verifyNoMoreInteractions(metrics)
    }

    "return a CreateEndpointStatus(\"Failed\") when the SNS client fails" in {

      when(client.createEndpoint(androidEndpoint.registrationToken, applicationArn)).thenReturn(failed(new RuntimeException("oh noes!")))

      val service = newService(Map.empty[String, String])
      val result = await(service.createEndpoints(Seq(androidEndpoint)))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.failure(androidEndpoint.registrationToken, "oh noes!")
      verify(metrics, times(1)).endpointCreationFailure()
      verify(metrics, times(1)).unknownOsFailure()
      verifyNoMoreInteractions(metrics)
    }

    "return a CreateEndpointStatus(\"Failed\") when the endpoint contains an unknown OS" in {
      val service = newService(Map(Android -> applicationArn))
      val result = await(service.createEndpoints(Seq(Endpoint("Baidu", "deviceToken"))))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.failure("deviceToken", "No platform application can be found for the os[Baidu]")
      verify(metrics, times(1)).endpointCreationFailure()
      verify(metrics, times(1)).unknownOsFailure()
      verifyNoMoreInteractions(metrics)
    }
  }
}
