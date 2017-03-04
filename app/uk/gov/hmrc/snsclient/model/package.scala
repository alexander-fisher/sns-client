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


  case class BatchDeliveryStatus(deliveryStatuses:Map[String, String])

  object BatchDeliveryStatus {

    def apply(statuses: Seq[DeliveryStatus]): BatchDeliveryStatus = {
      BatchDeliveryStatus(statuses.map(p => p.notificationId -> p.status) toMap)
    }

    implicit val format: OFormat[BatchDeliveryStatus] =
      Json.format[BatchDeliveryStatus]
  }


  case class DeliveryStatus(notificationId:String, status: String)

  object DeliveryStatus {

    def success(notificationId:String) = DeliveryStatus(notificationId:String, "SUCCESS")
    def failure(notificationId:String) = DeliveryStatus(notificationId:String, "FAILURE")

    implicit val format: OFormat[DeliveryStatus] =
      Json.format[DeliveryStatus]
  }

  case class CreateEndpointStatus(id:String, endpointArn: String)

  object CreateEndpointStatus {

    def success(notificationId:String, endpointArn:String) = CreateEndpointStatus(notificationId:String, "SUCCESS")
    def failure(notificationId:String) = CreateEndpointStatus(notificationId:String, "FAILURE")

    implicit val format: OFormat[CreateEndpointStatus] =
      Json.format[CreateEndpointStatus]
  }

  case class Endpoint(id:String, applicationArn: String, deviceToken: String)

  object Endpoint {
    implicit val format: OFormat[Endpoint] =
      Json.format[Endpoint]
  }
}
