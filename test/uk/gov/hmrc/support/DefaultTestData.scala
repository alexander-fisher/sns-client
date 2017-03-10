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

import java.util.UUID

import play.api.Configuration
import play.api.test.FakeRequest
import uk.gov.hmrc.snsclient.aws.sns.SnsConfiguration
import uk.gov.hmrc.snsclient.model.{Endpoint, Notification}

trait DefaultTestData {

  import ConfigKeys._

  def postSnsRequest(url: String) = FakeRequest("POST", url).withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json")

  val defaultConfig = Map(
    gcmOsKey -> "Android",
    gcmApiKey -> "test-apiKey",
    awsAccessKey -> "test-accessKey",
    awsSecretKey -> "test-secret",
    gcmApplicaitonArnKey -> "test-applicationArn",
    awsRegionKey -> "eu-west-1"
  )

  val defaultSnsConfiguration = new SnsConfiguration(Configuration from defaultConfig)
  val androidNotification : Notification = androidNotification(UUID.randomUUID().toString)
  def androidNotification(id:String) = Notification("registrationToken", "Tax is fun!", "Android", id)
  val androidEndpoint = Endpoint(defaultSnsConfiguration.gcmConfiguration.get.osName, "deviceToken")
}

object ConfigKeys {
  val gcmOsKey = "aws.platform.gcm.osName"
  val gcmApiKey = "aws.platform.gcm.apiKey"
  val gcmApplicaitonArnKey = "aws.platform.gcm.applicationArn"
  val awsAccessKey = "aws.accessKey"
  val awsSecretKey = "aws.secret"
  val awsRegionKey = "aws.region"
}
