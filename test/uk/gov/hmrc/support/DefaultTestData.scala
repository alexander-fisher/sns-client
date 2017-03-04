package uk.gov.hmrc.support

import play.api.test.FakeRequest
import uk.gov.hmrc.snsclient.controllers.routes
import uk.gov.hmrc.snsclient.model.Notification

trait DefaultTestData {

  val defaultNotification = Notification("registrationToken", "Tax is fun!", "Android", "GUID")

  val sendNotificationRequest   = FakeRequest("POST", routes.NotificationController.notifications().url).withHeaders("Content-Type" -> "application/json", "Accept" -> "application/vnd.hmrc.1.0+json")


}
