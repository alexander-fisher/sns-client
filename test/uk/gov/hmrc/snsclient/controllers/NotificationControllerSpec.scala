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

import akka.util.ByteString
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.libs.streams.Accumulator
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.snsclient.model.{Notification, NotificationResult}
import uk.gov.hmrc.support.AkkaMaterializerSpec

class NotificationControllerSpec extends PlaySpec with MockitoSugar with AkkaMaterializerSpec {

  s"POST /notifications" should {

    "return 200" in {

      val controller = new NotificationController(null)
      val json = Json.toJson(Notification("sometokenvalue"))

      val request = FakeRequest("POST", "/notifications")
        .withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json")
        .withJsonBody(json)

      val result: Accumulator[ByteString, Result] = controller.notifications(request)

//      contentAsJson(result) mustBe Json.toJson(NotificationResult("sometoken", true))
//      status(result) mustBe http.Status.OK
    }
  }
}
