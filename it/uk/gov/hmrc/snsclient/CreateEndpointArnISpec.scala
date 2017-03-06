package uk.gov.hmrc.snsclient

import java.util.UUID

import org.scalatest.concurrent.ScalaFutures
import play.api.libs.ws.WSClient
import uk.gov.hmrc.snsclient.model._

class CreateEndpointArnISpec extends ControllerSpec with ScalaFutures {


  val endpoint = Endpoint(
    UUID.randomUUID().toString,
    "Android",
    "einB0O0Mj9c:APA91bFvoRCQ0FdrYy0UBKgy84vTBvL3YEUDFnVyzYRQQ63raJg91rN2VIzQrkBfmeIZkev_uX0eSZSgWjPzIEuVC2yDMi8RIhwLu8FZtIgpngHHyelEKh3GrjfsIE5zcHSWkuF3_3bM")


  "Create a PlatformEndpointArn" should {

    "register that endpoint" in {

      val wsClient = app.injector.instanceOf[WSClient]
//      await(wsClient.url("http://localhost:8243/sns-client/endpoints").post(Endpoints(Seq(endpoint))))
    }
  }
}
