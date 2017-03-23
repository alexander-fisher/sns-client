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
import scala.language.postfixOps

case class Endpoint(os: String, registrationToken: String)
case class CreateEndpointStatus(id: String, endpointArn: Option[String] = None)

object CreateEndpointStatus {
  def success(id: String, endpointArn: String) = CreateEndpointStatus(id: String, Some(endpointArn))
  def failure(id: String, message: String = "") = CreateEndpointStatus(id: String, None)
}

case class Notifications(notifications: Seq[Notification])
case class Notification(endpointArn: String, message: String, id: String)
case class DeliveryStatus(id: String, status: String, error: Option[String])
case class BatchDeliveryStatus(deliveryStatuses: Map[String, String])

object BatchDeliveryStatus {
  def apply(statuses:Seq[DeliveryStatus]) : BatchDeliveryStatus = {
    BatchDeliveryStatus(statuses map(p => p.id -> p.status) toMap)
  }
}

object DeliveryStatus {
  def success(notificationId: String) = DeliveryStatus(notificationId: String, "Success", None)
  def failure(id: String, message: String = "") = DeliveryStatus(id: String, "Failed", Some(message))
  def disabled(id: String, message: String = "") = DeliveryStatus(id: String, "Disabled", None)
}