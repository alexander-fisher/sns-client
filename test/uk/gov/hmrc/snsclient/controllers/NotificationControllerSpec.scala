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
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.Json._
import play.api.test.Helpers._
import uk.gov.hmrc.snsclient.aws.sns.SnsApi
import uk.gov.hmrc.snsclient.model.{BatchDeliveryStatus, DeliveryStatus, Notification, Notifications}
import uk.gov.hmrc.support.{ControllerSpec, DefaultTestData}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future._


@RunWith(classOf[JUnitRunner])
class NotificationControllerSpec extends ControllerSpec with DefaultTestData {

  private val sns = resettingMock[SnsApi]
  private val controller   = new NotificationController(sns)

  s"POST" should {

    "return 200 if the Notification is successfully delivered to SNS" in {

      when(sns.publish(any[Seq[Notification]])(any[ExecutionContext]))
        .thenReturn(successful(Seq(DeliveryStatus.success(defaultNotification.id))))

      val requestJson = toJson(Notifications(Seq(defaultNotification)))

      val result = call(controller.sendNotifications, sendNotificationRequest.withJsonBody(requestJson))

      status(result) mustEqual OK
      contentAsJson(result) mustEqual toJson(BatchDeliveryStatus(Seq(DeliveryStatus.success("GUID"))))
    }
  }
}
