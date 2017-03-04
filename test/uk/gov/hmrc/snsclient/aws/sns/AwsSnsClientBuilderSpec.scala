package uk.gov.hmrc.snsclient.aws.sns

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.Configuration
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.support.DefaultTestData

@RunWith(classOf[JUnitRunner])
class AwsSnsClientBuilderSpec extends UnitSpec with DefaultTestData {


  "AwsSnsClientBuilder" should {

    val configuration: SnsConfiguration = new SnsConfiguration(Configuration from Map(
      "aws.accessKey" -> "theAccessKey",
      "aws.secret" -> "theSecret",
      "aws.signingRegion" -> "theSigningRegion",
      "aws.serviceEndpoint" -> "theServiceEndpoint")
    )

    val builder = new AwsSnsClientBuilder(configuration)

    "populates AWS credentials from SnsConfiguration" in {
      val credentials = builder.credentials.getCredentials
      credentials.getAWSAccessKeyId shouldBe configuration.accessKey
      credentials.getAWSSecretKey shouldBe configuration.secret
    }

    "populates AWS endpoint from SnsConfiguration" in {
      val endpoint = builder.endpoint
      endpoint.getServiceEndpoint shouldBe configuration.serviceEndpoint
      endpoint.getSigningRegion shouldBe configuration.signingRegion
    }
  }
}
