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

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.snsclient.aws.sns.SnsApi
import uk.gov.hmrc.snsclient.metrics.Metrics
import uk.gov.hmrc.snsclient.model.JsonFormats._
import uk.gov.hmrc.snsclient.model._

@Singleton
class NotificationController @Inject()(sns: SnsApi, metrics: Metrics) extends BaseController {

  val sendNotifications: Action[Seq[Notification]] = Action.async(parse.json[Seq[Notification]]) { implicit req =>
    sns.publish(req.body)(defaultContext).map {
      (statuses: Seq[DeliveryStatus]) =>
        metrics.batchPublicationSuccess()
        Ok(Json.toJson(statuses map(p => p.id -> p.status) toMap))
    } recover {
      case e =>
        Logger.error(s"Batch notification publication failed: [${e.getMessage}]")
        metrics.batchPublicationFailure()
        BadRequest(Json.toJson(Map("error" -> s"Batch notification publication failed: [${e.getMessage}]")))
    }
  }
}


