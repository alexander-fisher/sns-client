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
import org.scalatest.junit.JUnitRunner
import play.api.mvc.Result
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.snsclient.model.Notification


@RunWith(classOf[JUnitRunner])
class ValidationSpec extends UnitSpec with Validation {

  private implicit class ResultChecker(r: Result) {
    def is(r1: Result) = status(r1) shouldBe status(r)
  }

  private def responseFor(notification: Notification): Result = {
    checkForErrors(notification).head
  }

  private val validInvite: Notification = Notification("token")


  "checkForErrors" should {

    "fail with BadRequest if the token is empty" in {
      responseFor(validInvite.copy(token = "")) is BadRequest
    }

    "pass when the token field is non-empty" in {
      checkForErrors(validInvite) shouldBe Nil
    }
  }
}