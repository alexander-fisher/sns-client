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

import javax.inject.{Inject, Singleton}

import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.model._
import play.api.Logger
import play.api.libs.json._
import uk.gov.hmrc.snsclient.aws.AwsAsyncSupport
import uk.gov.hmrc.snsclient.model._

import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class SnsClientScalaAdapter @Inject()(client: AmazonSNSAsync) extends AwsAsyncSupport {

  def publish(notification: Notification)(implicit ctx: ExecutionContext): Future[PublishResult] =
    withAsyncHandler[PublishRequest, PublishResult] { handler =>

      val request = (notification.os, notification.messageId) match {
        case ("windows", Some(messageId)) =>
          new PublishRequest()
            .withMessageStructure("json")
            .withMessage(buildWnsMessage(notification.message, messageId).toString())
            .withMessageAttributes(buildWnsMessageAttributes())
            .withTargetArn(notification.endpointArn)
        case ("windows", None) =>
          new PublishRequest()
            .withMessage(notification.message)
            .withTargetArn(notification.endpointArn)
        case ("ios" | "android", messageId) =>
          new PublishRequest()
            .withMessageStructure("json")
            .withMessage(buildFcmMessage(notification.message, messageId).toString())
            .withTargetArn(notification.endpointArn)
        case (platform, _) =>
          throw new IllegalArgumentException(s"$platform is not supported")
      }

      Logger.info(request.toString)

      client.publishAsync(request, handler)
    }


  def createEndpoint(registrationToken: String, platformApplicationArn: String)(implicit ctx: ExecutionContext): Future[CreatePlatformEndpointResult] =
    withAsyncHandler[CreatePlatformEndpointRequest, CreatePlatformEndpointResult] { handler =>

      val request = new CreatePlatformEndpointRequest()
        .withPlatformApplicationArn(platformApplicationArn)
        .withToken(registrationToken)

      client.createPlatformEndpointAsync(request, handler)
    }

  def buildFcmMessage(message: String, messageId: Option[String]): JsObject = JsObject(
    Seq(
      "GCM" -> JsString(buildFcmPayload(message, messageId).toString())
    ))

  def buildFcmPayload(message: String, messageId: Option[String]): JsObject = messageId match {
    case Some(id) => JsObject(Seq(
      "data" -> JsObject(Seq(
        "messageId" -> JsString(id),
        "text" -> JsString(message)
      ))
    ))
    case None => JsObject(Seq(
      "notification" -> JsObject(Seq(
        "text" -> JsString(message)
      ))
    ))
  }

  def buildWnsMessage(message: String, messageId: String): JsObject = JsObject(
    Seq(
      "WNS" -> JsString(buildWnsPayload(message, messageId).toString())
    ))

  def buildWnsPayload(message: String, messageId: String): JsObject = JsObject(
    Seq(
      "notification" -> JsObject(Seq(
        "body" -> JsString(message)
      )),
      "data" -> JsObject(Seq(
        "messageId" -> JsString(messageId)
      ))
    ))

  def buildWnsMessageAttributes(): Map[String, MessageAttributeValue] = Map(
    "AWS.SNS.MOBILE.WNS.Type" -> new MessageAttributeValue().withDataType("String").withStringValue("wns/raw")
  )
}
