package uk.gov.hmrc.snsclient.aws.sns

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.Configuration
import uk.gov.hmrc.play.test.UnitSpec

@RunWith(classOf[JUnitRunner])
class AwsSnsClientBuilderSpec extends UnitSpec {

  private def loadConfig(properties: Map[String, String]) = Configuration from properties

  def defaultConfig = Map(
    "aws.accessKey" -> "v1",
    "aws.secret" -> "v2",
    "aws.signingRegion" -> "v3",
    "aws.serviceEndpoint" -> "v4")

  "AwsSnsClientBuilder" should {

    val configuration: SnsConfiguration = new SnsConfiguration(loadConfig(defaultConfig))

    "populates AWS credentials from SnsConfiguration" in new AwsSnsClientBuilder {

      val creds: AWSStaticCredentialsProvider = credentials(configuration)
      creds.getCredentials.getAWSAccessKeyId shouldBe configuration.accessKey
      creds.getCredentials.getAWSSecretKey shouldBe configuration.secret
    }

    "populates AWS endpoint from SnsConfiguration" in new AwsSnsClientBuilder {

      val endpoint: EndpointConfiguration = endpoint(configuration)
      endpoint.getServiceEndpoint shouldBe configuration.serviceEndpoint
      endpoint.getSigningRegion shouldBe configuration.signingRegion
    }
  }
}
