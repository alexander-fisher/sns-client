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

package uk.gov.hmrc.snsclient.aws.sns

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.model.{CreatePlatformEndpointRequest, CreatePlatformEndpointResult, PublishRequest, PublishResult}
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.support.{DefaultTestData, ResettingMockitoSugar}

class SnsClientScalaAdapterSpec extends UnitSpec with ResettingMockitoSugar with DefaultTestData {


  private val client = resettingMock[AmazonSNSAsync]
  def adapter = new SnsClientScalaAdapter(client)


  "SnsClientScalaAdapter" should {

    "publish" in {

      val captor = ArgumentCaptor.forClass(classOf[PublishRequest])
      val handler = ArgumentCaptor.forClass(classOf[AsyncHandler[PublishRequest, PublishResult]])

      adapter.publish(androidNotification)
      verify(client).publishAsync(captor.capture(), handler.capture())
      captor.getValue.getMessage shouldBe androidNotification.message
      captor.getValue.getTargetArn shouldBe androidNotification.endpointArn
    }

    "createEndpoint" in {

      val captor = ArgumentCaptor.forClass(classOf[CreatePlatformEndpointRequest])
      val handler = ArgumentCaptor.forClass(classOf[AsyncHandler[CreatePlatformEndpointRequest, CreatePlatformEndpointResult]])

      adapter.createEndpoint("reg-token", "platform-application-arn")
      verify(client).createPlatformEndpointAsync(captor.capture(), handler.capture())
      captor.getValue.getToken shouldBe "reg-token"
      captor.getValue.getPlatformApplicationArn shouldBe "platform-application-arn"
    }
  }
}
