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

  import uk.gov.hmrc.snsclient.config.ConfigKeys._

  def postSnsRequest(url: String) = FakeRequest("POST", url).withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json")

  val defaultConfig = Map(
    s"$awsEncryptionKey.key" -> "gvBoGdgzqG1AarzF1LY0zQ==",
    gcmApiKey -> "vAZzRIMPUrzpp7f+9Z3DyboiY3QFeWHhfTS90aI0IDbWuRc=",
    awsAccessKey -> "cIN2VG9Gkf5EInwn6ZQXDgIBxGd0yQW2RJuxO8+aBmjno/8=",
    awsSecretKey -> "1JVN4ZxHpWo4AuTwsdKyE9Px7HvrAvp3cq6wZNwKgw9ciR0=",
    gcmOsKey -> "android",
    gcmApplicaitonArnKey -> "test-applicationArn",
    awsRegionKey -> "eu-west-1",
    awsRegionOverrideKey -> "stubbed-aws-sns",
    awsStubbingKey -> true
  )

  val defaultSnsConfiguration = new SnsConfiguration(Configuration from defaultConfig)

  val windowsNotification : Notification = simpleNotification(UUID.randomUUID().toString, "windows")
  val windowsNotificationWithMessageId : Notification = notificationWithMessageId(UUID.randomUUID().toString, "windows")
  val androidNotificationWithMessageId : Notification = notificationWithMessageId(UUID.randomUUID().toString, "android")
  val iosNotificationWithMessageId : Notification = notificationWithMessageId(UUID.randomUUID().toString, "ios")

  def simpleNotification(id:String, os: String) = Notification(id, "registrationToken", os, "Tax is fun!", None)
  def notificationWithMessageId(id:String, os: String) = Notification(id, "registrationToken", os, "Tax is fun!", Option("123"))

  val androidEndpoint = Endpoint(defaultSnsConfiguration.gcmConfiguration.get.osName, "android-registration-token")
}
