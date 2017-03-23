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

package uk.gov.hmrc.snsclient.controllers

import org.junit.runner.RunWith
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => eqs}
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.Json._
import play.api.test.Helpers._
import uk.gov.hmrc.snsclient.aws.sns.SnsApi
import uk.gov.hmrc.snsclient.metrics.Metrics
import uk.gov.hmrc.snsclient.model._
import uk.gov.hmrc.support.{ControllerSpec, DefaultTestData}
import uk.gov.hmrc.snsclient.model.JsonFormats._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future._


@RunWith(classOf[JUnitRunner])
class EndpointControllerSpec extends ControllerSpec with DefaultTestData {

  private val sns         = resettingMock[SnsApi]
  private val metrics     = resettingMock[Metrics]
  private val controller  = new EndpointsController(sns, metrics)
  private val url         = routes.EndpointsController.createEndpoints().url

  s"POST" should {

    val androidEndpointStatus = CreateEndpointStatus.success(androidEndpoint.registrationToken, "endpointArn")

    "return 200 with Some(EndpointArn) if the Endpoint is returned by SNS" in {

      when(sns.createEndpoints(eqs(Seq(androidEndpoint)))(any[ExecutionContext])).thenReturn(successful(Seq(androidEndpointStatus)))

      val result = call(controller.createEndpoints, postSnsRequest(url).withJsonBody(toJson(Seq(androidEndpoint))))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(Seq(androidEndpointStatus) map(p => p.id-> p.endpointArn) toMap)
      verify(metrics, times(1)).endpointCreationSuccess()
    }

    "return 200 with when more than 1 endpoint ARN is created by SNS" in {

      val endpoint = androidEndpoint.copy(registrationToken = "registration-token")
      val endpointStatus = CreateEndpointStatus.success(endpoint.registrationToken, "endpointArn")

      when(sns.createEndpoints(eqs(Seq(androidEndpoint, endpoint)))(any[ExecutionContext]))
        .thenReturn(successful(Seq(androidEndpointStatus, endpointStatus)))

      val result = call(controller.createEndpoints, postSnsRequest(url).withJsonBody(toJson(Seq(androidEndpoint, endpoint))))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(Seq(androidEndpointStatus, endpointStatus) map(p => p.id-> p.endpointArn) toMap)
      verify(metrics, times(1)).endpointCreationSuccess()
    }

    "return 200 with the None if the Endpoint if SNS cannot create the Endpoint" in {

      when(sns.createEndpoints(any[Seq[Endpoint]])(any[ExecutionContext])).thenReturn(successful(Seq(androidEndpointStatus)))

      val result = call( controller.createEndpoints, postSnsRequest(url).withJsonBody(toJson(Seq(androidEndpoint))))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(Seq(androidEndpointStatus) map(p => p.id-> p.endpointArn) toMap)
      verify(metrics, times(1)).endpointCreationSuccess()
    }


    "return 200 when 1 of 2 endpoint ARNs are created by SNS" in {

      val endpoint = androidEndpoint.copy(registrationToken = "registration-token")
      val endpointStatus = CreateEndpointStatus.failure(endpoint.registrationToken, "Something went horribly wrong")

      when(sns.createEndpoints(eqs(Seq(androidEndpoint, endpoint)))(any[ExecutionContext]))
        .thenReturn(successful(Seq(androidEndpointStatus, endpointStatus)))

      val result = call(controller.createEndpoints, postSnsRequest(url).withJsonBody(toJson(Seq(androidEndpoint, endpoint))))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(Seq(androidEndpointStatus, endpointStatus) map(p => p.id-> p.endpointArn) toMap)
      verify(metrics, times(1)).endpointCreationSuccess()
      verifyNoMoreInteractions(metrics)
    }

    "return 400 when the batch fails" in {

      when(sns.createEndpoints(any[Seq[Endpoint]])(any[ExecutionContext]))
        .thenReturn(Future failed new RuntimeException("Something nasty occurred processing these futures"))

      val result = call(controller.createEndpoints, postSnsRequest(url).withJsonBody(toJson(Seq(androidEndpoint))))

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustEqual toJson(Map("error" -> "Batch creation of endpoints failed: [Something nasty occurred processing these futures]"))
      verify(metrics, times(1)).batchEndpointCreationFailure()
      verifyNoMoreInteractions(metrics)
    }
  }
}
