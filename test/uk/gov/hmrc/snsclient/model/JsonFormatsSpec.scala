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

package uk.gov.hmrc.snsclient.model


import play.api.libs.json.{JsString, JsSuccess, Json}
import uk.gov.hmrc.play.test.UnitSpec

class JsonFormatsSpec extends UnitSpec {

  "JsonFormatsSpec" should {

    val stringToSomeString = Map("some" -> Some("value"))

    "mapReads" in {
      Json.toJson(stringToSomeString) shouldBe Json.obj("some" ->  JsString("value"))
    }

    "mapWrites" in {
      import JsonFormats._
      Json.fromJson[Map[String, Option[String]]](Json.obj("some" ->  JsString("value"))) shouldBe JsSuccess(stringToSomeString)
    }
  }
}
