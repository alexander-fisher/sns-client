package uk.gov.hmrc.snsclient.aws.sns

import javax.inject.{Inject, Singleton}

import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.model.{CreatePlatformEndpointRequest, CreatePlatformEndpointResult, PublishRequest, PublishResult}
import uk.gov.hmrc.snsclient.aws.AwsAsyncSupport
import uk.gov.hmrc.snsclient.model.{Endpoint, Notification}

import scala.concurrent.ExecutionContext

@Singleton
class SnsClientScalaAdapter @Inject() (builder: AwsSnsClientBuilder) extends AwsAsyncSupport {

  val client: AmazonSNSAsync = builder getInstance

  def publish(notification: Notification)(implicit ctx:ExecutionContext) =
    withAsyncHandler[PublishRequest, PublishResult] { handler =>

      val request = new PublishRequest()
        .withMessage(notification.message)
        .withTargetArn(notification.targetArn)

      client.publishAsync(request, handler)
    }


  def createEndpoint(endpoint: Endpoint)(implicit ctx:ExecutionContext) =
    withAsyncHandler[CreatePlatformEndpointRequest, CreatePlatformEndpointResult] { handler =>

      val request = new CreatePlatformEndpointRequest()
        .withPlatformApplicationArn(endpoint.applicationArn)
        .withToken(endpoint.deviceToken)

      client.createPlatformEndpointAsync(request, handler)
    }
}