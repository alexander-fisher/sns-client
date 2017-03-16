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
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.snsclient.aws.sns.SnsApi
import uk.gov.hmrc.snsclient.metrics.Metrics
import uk.gov.hmrc.snsclient.model.JsonFormats._
import uk.gov.hmrc.snsclient.model.{BatchEndpointsStatus, Endpoints}

@Singleton
class EndpointsController @Inject() (sns:SnsApi, metrics:Metrics) extends BaseController {

  val createEndpoints: Action[Endpoints] = Action.async(parse.json[Endpoints]) { implicit req =>
    sns.createEndpoints(req.body.endpoints)(defaultContext).map { statuses =>
      metrics.endpointCreationSuccess()
      Ok(Json.toJson(BatchEndpointsStatus(statuses)))
    } recover {
      case e =>
        Logger.warn(s"Batch creation of endpoints failed ${e.getStackTrace.mkString("\n")}")
        metrics.batchEndpointCreationFailure()
        BadRequest(Json.toJson(Map("error" -> s"Batch creation of endpoints failed: [${e.getMessage}]")))
    }
  }
}