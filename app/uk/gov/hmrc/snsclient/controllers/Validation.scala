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

import play.api.mvc.{Result, Results}
import uk.gov.hmrc.snsclient.model.Notification

trait Validation extends Results {

  private type Validation = (Notification) => Option[Result]

  private def hasAToken: (Notification) => Option[Result] = (notification) => {
   if (notification.endpointArn.isEmpty) Some( ErrorResults.MissingToken ) else None
  }

  def checkForErrors(notification: Notification) : Seq[Result] =
    Seq(hasAToken).flatMap(fn => fn(notification))
}
