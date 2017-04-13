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

import play.api.libs.functional.syntax._
import play.api.libs.json._

object JsonFormats {

  implicit val mapWrites = new Writes[Map[String, Option[String]]] {
    override def writes(m: Map[String, Option[String]]): JsValue = {
      JsObject(m.map {
        case (k, Some(v)) => k -> JsString(v)
        case (k, None) => k -> JsNull
      })
    }
  }

  implicit val mapReads = new Reads[Map[String, Option[String]]] {
    override def reads(json: JsValue): JsResult[Map[String, Option[String]]] = {
      JsSuccess(json.as[JsObject].fields.map(toOptionTuple).toMap)
    }
  }

  implicit val tokenReads: Reads[Endpoint] = (
    (JsPath \ "device" \ "os").read[String] and
      (JsPath \ "token").read[String]
    ) (Endpoint.apply _)

  implicit val tokenWrites: Writes[Endpoint] = (
    (JsPath \ "device" \ "os").write[String] and
      (JsPath \ "token").write[String]
    ) (unlift(Endpoint.unapply))

  private def toOptionTuple(kv: (String,JsValue)) : (String, Option[String]) =
    (kv._1, kv._2) match {
      case (k, JsString(v)) => k -> Some(v)
      case (k, JsNull)      => k -> None
      case (k,v) => throw new RuntimeException(s"Failed to read [$k -> $v] as String -> Option[String]")
  }


  implicit val mapOptionFormat = Format(mapReads, mapWrites)
  implicit val createEndpointStatusFormat = Json.format[CreateEndpointStatus]
  implicit val notificationFormat = Json.format[Notification]
  implicit val deliveryStatusFormat: OFormat[DeliveryStatus] = Json.format[DeliveryStatus]
}
