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
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.Json._
import play.api.test.Helpers._
import uk.gov.hmrc.snsclient.aws.sns.SnsApi
import uk.gov.hmrc.snsclient.metrics.Metrics
import uk.gov.hmrc.snsclient.model.JsonFormats._
import uk.gov.hmrc.snsclient.model._
import uk.gov.hmrc.support.{ControllerSpec, DefaultTestData}

import scala.concurrent.Future._
import scala.concurrent.{ExecutionContext, Future}


@RunWith(classOf[JUnitRunner])
class NotificationControllerSpec extends ControllerSpec with DefaultTestData {

  private val sns = resettingMock[SnsApi]
  private val metrics = resettingMock[Metrics]
  private val controller   = new NotificationController(sns, metrics)

  private val notificationsUrl: String = routes.NotificationController.sendNotifications().url


  s"POST to $notificationsUrl" should {

    "return 200 and the delivery status for a single successful notification" in {

      val response = Seq(DeliveryStatus.success(windowsNotification.id))

      when(sns.publish(any[Seq[Notification]])(any[ExecutionContext])).thenReturn(successful(response))

      val request = toJson(Seq(windowsNotification))

      val result = call(controller.sendNotifications, postSnsRequest(notificationsUrl).withJsonBody(request))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(response map(p => p.id -> p.status) toMap)
      verify(metrics, times(1)).batchPublicationSuccess()
      verifyNoMoreInteractions(metrics)
    }


    "return 200 and the delivery status for 2 successful notification" in {

      val response = Seq(DeliveryStatus.success(windowsNotification.id), DeliveryStatus.success(windowsNotification.id))

      when(sns.publish(any[Seq[Notification]])(any[ExecutionContext])).thenReturn(successful(response))

      val request = toJson(Seq(windowsNotification, windowsNotification))

      val result = call(controller.sendNotifications, postSnsRequest(notificationsUrl).withJsonBody(request))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(response map(p => p.id -> p.status) toMap)
      verify(metrics, times(1)).batchPublicationSuccess()
      verifyNoMoreInteractions(metrics)
    }


    "return 200 and the delivery status for 1 successful and 1 failed notification" in {

      val response = Seq(DeliveryStatus.success(windowsNotification.id), DeliveryStatus.failure(windowsNotification.id))

      when(sns.publish(any[Seq[Notification]])(any[ExecutionContext])).thenReturn(successful(response))

      val request = toJson(Seq(windowsNotification, windowsNotification))

      val result = call(controller.sendNotifications, postSnsRequest(notificationsUrl).withJsonBody(request))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(response map(p => p.id -> p.status) toMap)
      verify(metrics, times(1)).batchPublicationSuccess()
      verifyNoMoreInteractions(metrics)
    }


    "return 200 and DISABLED when the endpointArn has been disabled" in {

      val disabledResponse = Seq(DeliveryStatus.disabled(windowsNotification.id))

      when(sns.publish(any[Seq[Notification]])(any[ExecutionContext]))
        .thenReturn(successful(disabledResponse))

      val result = call(
        controller.sendNotifications,
        postSnsRequest(notificationsUrl)
          .withJsonBody(toJson(Seq(windowsNotification)))
      )

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(disabledResponse map(p => p.id -> p.status) toMap)

      verify(metrics, times(1)).batchPublicationSuccess()
      verifyNoMoreInteractions(metrics)
    }

    "return 400 when the batch fails" in {

      when(sns.publish(any[Seq[Notification]])(any[ExecutionContext]))
        .thenReturn(Future failed new RuntimeException("Something nasty occurred processing these futures"))

      val request = toJson(Seq(windowsNotification, windowsNotification))

      val result = call(controller.sendNotifications, postSnsRequest(notificationsUrl).withJsonBody(request))

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustEqual toJson(Map("error" -> "Batch notification publication failed: [Something nasty occurred processing these futures]"))
      verify(metrics, times(1)).batchPublicationFailure()
      verifyNoMoreInteractions(metrics)
    }
  }
}
