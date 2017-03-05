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

package uk.gov.hmrc.snsclient

import play.api.libs.json.{Json, OFormat}

package model {

  import scala.language.postfixOps

  case class Notification(targetArn:String, message:String, os:String, id:String)

  object Notification {
    implicit val format: OFormat[Notification] =
      Json.format[Notification]
  }

  case class Notifications ( notifications: Seq[Notification] )

  object Notifications {
    implicit val format: OFormat[Notifications] =
      Json.format[Notifications]
  }


  case class Endpoints ( endpoints: Seq[Endpoint] )

  object Endpoints {
    implicit val format: OFormat[Endpoints] =
      Json.format[Endpoints]
  }


  case class BatchEndpointsStatus(deliveryStatuses:Map[String, CreateEndpointStatus])

  object BatchEndpointsStatus {

    def apply(statuses: Seq[CreateEndpointStatus]): BatchEndpointsStatus = {
      BatchEndpointsStatus(statuses.map(p => p.id -> p) toMap)
    }

    implicit val format: OFormat[BatchEndpointsStatus] =
      Json.format[BatchEndpointsStatus]
  }


  case class BatchDeliveryStatus(deliveryStatuses:Map[String, DeliveryStatus])

  object BatchDeliveryStatus {

    def apply(statuses: Seq[DeliveryStatus]): BatchDeliveryStatus = {
      BatchDeliveryStatus(statuses.map(p => p.notificationId -> p) toMap)
    }

    implicit val format: OFormat[BatchDeliveryStatus] =
      Json.format[BatchDeliveryStatus]
  }


  case class DeliveryStatus(notificationId:String, status: String, error:Option[String])

  object DeliveryStatus {

    def success(notificationId:String) = DeliveryStatus(notificationId:String, "SUCCESS", None)
    def failure(notificationId:String, message:String = "") = DeliveryStatus(notificationId:String, "FAILURE", Some(message))

    implicit val format: OFormat[DeliveryStatus] =
      Json.format[DeliveryStatus]
  }

  case class CreateEndpointStatus(id:String, endpointArn: String, error:Option[String])

  object CreateEndpointStatus {

    def success(notificationId:String, endpointArn:String) = CreateEndpointStatus(notificationId:String, "SUCCESS", None)
    def failure(notificationId:String, message:String = "") = CreateEndpointStatus(notificationId:String, "FAILURE", Some(message))

    implicit val format: OFormat[CreateEndpointStatus] =
      Json.format[CreateEndpointStatus]
  }

  case class Endpoint(id:String, os:String, deviceToken: String)



  object Endpoint {
    implicit val format: OFormat[Endpoint] =
      Json.format[Endpoint]
  }
}
