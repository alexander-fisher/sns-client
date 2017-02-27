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

import play.api.mvc.{Action, Result}
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.snsclient.model.Notification
import uk.gov.hmrc.snsclient.service.SNSService

import scala.concurrent.Future

@Singleton
class NotificationController @Inject() (sns:SNSService) extends BaseController {

  def notifications = Action.async(parse.json) { implicit request =>
    withJsonBody[Notification] { n =>
      val r : Future[Result] = Future successful Ok("")
      r
    }
  }
}