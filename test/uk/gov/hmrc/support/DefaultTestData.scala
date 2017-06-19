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
import uk.gov.hmrc.snsclient.model.NativeOS.{Android, Ios, Windows}
import uk.gov.hmrc.snsclient.model.{Endpoint, NativeOS, Notification}

trait DefaultTestData {

  import uk.gov.hmrc.snsclient.config.ConfigKeys._

  def postSnsRequest(url: String) = FakeRequest("POST", url).withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json")

  val defaultConfig = Map(
    awsAccessKey -> "test-accessKey",
    awsSecretKey -> "test-secret",
    apiKey.replace("PLATFORM", Android) -> "test-apiKey",
    applicationArnKey.replace("PLATFORM", Android) -> "android-test-applicationArn",
    apiKey.replace("PLATFORM", Windows) -> "test-apiKey",
    applicationArnKey.replace("PLATFORM", Windows) -> "windows-test-applicationArn",
    awsRegionKey -> "eu-west-1",
    awsRegionOverrideKey -> "stubbed-aws-sns",
    awsStubbingKey -> true
  )

  val defaultSnsConfiguration = new SnsConfiguration(Configuration from defaultConfig)

  val windowsNotification: Notification = simpleNotification(UUID.randomUUID().toString, Windows)
  val windowsNotificationWithMessageId: Notification = notificationWithMessageId(UUID.randomUUID().toString, Windows)
  val androidNotification: Notification = simpleNotification(UUID.randomUUID().toString, Android)
  val androidNotificationWithMessageId: Notification = notificationWithMessageId(UUID.randomUUID().toString, Android)
  val iosNotification: Notification = simpleNotification(UUID.randomUUID().toString, Ios)
  val iosNotificationWithMessageId: Notification = notificationWithMessageId(UUID.randomUUID().toString, Ios)

  def simpleNotification(id: String, os: String) = Notification(id, "registrationToken", os, "Tax is fun!", None)

  def notificationWithMessageId(id: String, os: String) = Notification(id, "registrationToken", os, "Tax is fun!", Option("123"))

  val androidEndpoint = Endpoint(Android, "android-registration-token")
}
