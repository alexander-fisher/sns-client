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

package uk.gov.hmrc.support

import play.api.Configuration
import play.api.test.FakeRequest
import uk.gov.hmrc.snsclient.aws.sns.SnsConfiguration
import uk.gov.hmrc.snsclient.model.{Endpoint, Notification}

trait DefaultTestData {

  def postSnsRequest(url :String) = FakeRequest("POST", url).withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json")

  val defaultConfig = Map(
    "aws.platform.gcm.osName" -> "Android",
    "aws.platform.gcm.applicationName" -> "gcmApplicationName",
    "aws.platform.gcm.registrationId" -> "gcmRegistrationId",
    "aws.platform.gcm.serverApiKey" -> "gcmServerApiKey",
    "aws.accessKey" -> "theAccessKey",
    "aws.secret" -> "theSecret",
    "aws.signingRegion" -> "theSigningRegion",
    "aws.serviceEndpoint" -> "theServiceEndpoint")

  val defaultSnsConfiguration = new SnsConfiguration(Configuration from defaultConfig)
  val defaultNotification     = Notification("registrationToken", "Tax is fun!", "Android", "GUID")
  val defaultEndpoint         = Endpoint("id", defaultSnsConfiguration.gcmConfiguration.get.osName, "deviceToken")
}
