package uk.gov.hmrc.snsclient.aws.sns

import com.amazonaws.services.sns.model.{CreatePlatformEndpointResult, PublishResult}
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.snsclient.model.{CreateEndpointStatus, DeliveryStatus}
import uk.gov.hmrc.support.{DefaultTestData, ResettingMockitoSugar}

import scala.concurrent.{ExecutionContext, Future}

@RunWith(classOf[JUnitRunner])
class SnsServiceSpec extends UnitSpec with ResettingMockitoSugar with DefaultTestData with ScalaFutures {

  implicit val ctx: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
  private val client = resettingMock[SnsClientScalaAdapter]

  "SnsServiceSpec" should {


    "return a DeliveryStatus(\"SUCCESS\") when SNS client publishes successfully" in {

      when(client.publish(defaultNotification))
        .thenReturn(Future successful new PublishResult().withMessageId("message-id"))

      val service = new SnsService(client)
      val result = await(service.publish(Seq(defaultNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.success("GUID")
    }

    "return a DeliveryStatus(\"FAILURE\") when SNS client fails" in {

      when(client.publish(defaultNotification))
        .thenReturn(Future failed new RuntimeException("oh noes!"))

      val service = new SnsService(client)
      val result = await(service.publish(Seq(defaultNotification)))
      result.size shouldBe 1
      result.head shouldBe DeliveryStatus.failure("GUID", "oh noes!")

    }

    "return a CreateEndpointStatus(\"SUCCESS\") when the SNS client creates the application endpoint" in {

      val endpointResult = new CreatePlatformEndpointResult()
      endpointResult.setEndpointArn("endpoint-arn")

      when(client.createEndpoint(defaultEndpoint)).thenReturn(Future successful endpointResult)

      val service = new SnsService(client)
      val result = await(service.createEndpoint(Seq(defaultEndpoint)))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.success(defaultEndpoint.id, endpointResult.getEndpointArn)
    }

    "return a CreateEndpointStatus(\"FAILURE\") when the SNS client fails" in {

      when(client.createEndpoint(defaultEndpoint))
        .thenReturn(Future failed new RuntimeException("oh noes!"))

      val service = new SnsService(client)
      val result = await(service.createEndpoint(Seq(defaultEndpoint)))
      result.size shouldBe 1
      result.head shouldBe CreateEndpointStatus.failure(defaultEndpoint.id, "oh noes!")
    }
  }
}
