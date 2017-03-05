package uk.gov.hmrc.support

import play.api.Configuration
import play.api.test.FakeRequest
import uk.gov.hmrc.snsclient.aws.sns.SnsConfiguration
import uk.gov.hmrc.snsclient.controllers.routes
import uk.gov.hmrc.snsclient.model.{Endpoint, Notification}

trait DefaultTestData {

  val defaultNotification = Notification("registrationToken", "Tax is fun!", "Android", "GUID")


  val sendNotificationRequest   = FakeRequest("POST", routes.NotificationController.sendNotifications().url).withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json")

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

  val defaultEndpoint = Endpoint("id", defaultSnsConfiguration.gcmConfiguration.get.osName, "deviceToken")
}
