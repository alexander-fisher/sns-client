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
import uk.gov.hmrc.snsclient.model._
import uk.gov.hmrc.support.{ControllerSpec, DefaultTestData}
import uk.gov.hmrc.snsclient.model.JsonFormats._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future._


@RunWith(classOf[JUnitRunner])
class EndpointControllerSpec extends ControllerSpec with DefaultTestData {

  private val sns         = resettingMock[SnsApi]
  private val controller  = new EndpointsController(sns)
  private val url         = routes.EndpointsController.createEndpoints().url

  s"POST" should {

    val statusSuccessful = CreateEndpointStatus.success(androidNotification.id, "endpointArn")

    "return 200 with Some(EndpointArn) if the Endpoint is returned by SNS" in {

      when(sns.createEndpoint(eqs(Seq(androidEndpoint)))(any[ExecutionContext])).thenReturn(successful(Seq(statusSuccessful)))

      val result = call(controller.createEndpoints, postSnsRequest(url).withJsonBody(toJson(Endpoints(Seq(androidEndpoint)))))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(BatchEndpointsStatus(Seq(statusSuccessful)))
    }

    "return 200 with the None if the Endpoint if SNS cannot create the Endpoint" in {

      when(sns.createEndpoint(any[Seq[Endpoint]])(any[ExecutionContext])).thenReturn(successful(Seq(statusSuccessful)))

      val result = call( controller.createEndpoints, postSnsRequest(url).withJsonBody(toJson(Endpoints(Seq(androidEndpoint)))))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(BatchEndpointsStatus(Seq(statusSuccessful)))
    }
  }
}
