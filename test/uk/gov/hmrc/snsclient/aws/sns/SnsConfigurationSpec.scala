package uk.gov.hmrc.snsclient.aws.sns

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.Configuration
import uk.gov.hmrc.play.test.UnitSpec

@RunWith(classOf[JUnitRunner])
class SnsConfigurationSpec extends UnitSpec {

  private def loadConfig(properties: Map[String, String]) = Configuration from properties

  def defaultConfig = Map(
    "aws.accessKey" -> "v1",
    "aws.secret" -> "v2",
    "aws.signingRegion" -> "v3",
    "aws.serviceEndpoint" -> "v4")

  private def requiresTheKey(key: String) = {
    intercept[IllegalArgumentException] {
      new SnsConfiguration(loadConfig(defaultConfig - key))
    }.getMessage shouldBe s"property at [$key] was missing"
  }

  private def keyIsNotEmpty(key: String) = {
    intercept[IllegalArgumentException] {
      new SnsConfiguration(loadConfig(defaultConfig updated(key, "")))
    }.getMessage shouldBe s"property at [$key] was empty"
  }

  "SnsConfiguration" should {

    "reads config from application.conf" in {
      new SnsConfiguration(loadConfig(defaultConfig))
    }

    "requires an accessKey to be set" in {
      requiresTheKey("aws.accessKey")
      keyIsNotEmpty("aws.accessKey")
    }

    "requires an secret to be set" in {
      requiresTheKey("aws.secret")
      keyIsNotEmpty("aws.secret")
    }

    "requires an signingRegion to be set" in {
      requiresTheKey("aws.signingRegion")
      keyIsNotEmpty("aws.signingRegion")
    }

    "requires an serviceEndpoint to be set" in {
      requiresTheKey("aws.serviceEndpoint")
      keyIsNotEmpty("aws.serviceEndpoint")
    }
  }
}
