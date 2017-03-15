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

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{OFormat, _}

import scala.collection.Map
import scala.language.postfixOps

case class Notification(endpointArn: String, message: String, id: String)


object Notification {
  implicit val format: OFormat[Notification] = Json.format[Notification]
}

case class Notifications(notifications: Seq[Notification])
object Notifications {
  implicit val format: OFormat[Notifications] = Json.format[Notifications]
}

case class BatchDeliveryStatus(deliveryStatuses: Map[String, String])
object BatchDeliveryStatus {

  def apply(statuses: Seq[DeliveryStatus]): BatchDeliveryStatus = {
    BatchDeliveryStatus(statuses.map(p => p.notificationId -> p.status) toMap)
  }

  implicit val format: OFormat[BatchDeliveryStatus] = Json.format[BatchDeliveryStatus]
}

case class DeliveryStatus(notificationId: String, status: String, error: Option[String])
object DeliveryStatus {

  def success(notificationId: String) = DeliveryStatus(notificationId: String, "Success", None)
  def failure(notificationId: String, message: String = "") = DeliveryStatus(notificationId: String, "Failed", Some(message))
  def disabled(notificationId: String, message: String = "") = DeliveryStatus(notificationId: String, "Disabled", None)

  implicit val format: OFormat[DeliveryStatus] = Json.format[DeliveryStatus]
}


case class Endpoints(endpoints: Seq[Endpoint])
object Endpoints {
  implicit val format: OFormat[Endpoints] = Json.format[Endpoints]
}


case class BatchEndpointsStatus(deliveryStatuses: Map[String, Option[String]])
object BatchEndpointsStatus {

  def apply(statuses: Seq[CreateEndpointStatus]): BatchEndpointsStatus = {
    BatchEndpointsStatus(statuses.map(p => p.id -> p.endpointArn) toMap)
  }

  implicit val mapWrites: OWrites[Map[String, Option[String]]] = play.api.libs.json.Writes.mapWrites[Option[String]]
  implicit val mapReads: OReads[Map[String, Option[String]]] = play.api.libs.json.Writes.mapWrites[Option[String]]

  implicit val format: OFormat[BatchEndpointsStatus] =
    Json.format[BatchEndpointsStatus]


  implicit object OptionStringWrites extends OFormat[Option[String]] {
    def reads(json: JsValue) = json match {
      case JsString(s) => JsSuccess(Option(s))
      case JsNull => JsSuccess(None)
      case _ => JsError("expected Option[String]")
    }

    override def writes(o: Option[String]): JsValue = o match {
      case Some(v) => JsString(v)
      case None => JsNull
    }
  }
}

case class CreateEndpointStatus(id: String, endpointArn: Option[String] = None)
object CreateEndpointStatus {

  def success(notificationId:String, endpointArn:String) = CreateEndpointStatus(notificationId:String, Some(endpointArn))
  def failure(notificationId:String, message:String = "") = CreateEndpointStatus(notificationId:String, None)

  implicit val format: OFormat[CreateEndpointStatus] = Json.format[CreateEndpointStatus]
}

case class Endpoint(os: String, registrationToken: String)
object Endpoint {
  implicit val format: OFormat[Endpoint] = Json.format[Endpoint]
}



//  implicit object MapFormat extends OFormat[Map[String, Option[String]]] {
//    override def writes(m: Map[String, Option[String]]): JsObject = play.api.libs.json.Writes.mapWrites[Option[String]]
//    override def writes(m: Map[String, Option[String]]): JsObject = {
//      Json.obj(m.map {
//        case (k, Some(v)) => (k, JsString(v).asInstanceOf[JsValueWrapper])
//        case (k, None) => (k, JsNull.asInstanceOf[JsValueWrapper])
//      } toSeq :_*)
//    }


