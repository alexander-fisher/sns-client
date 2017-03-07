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

package uk.gov.hmrc.snsclient.sns

import javax.inject.{Inject, Singleton}

import play.api.{Configuration, Logger}

import scala.language.postfixOps


@Singleton
class SnsConfiguration @Inject()(configuration:Configuration) {

  private def required(key:String, configuration:Configuration): String = {
    configuration getString key match {
      case Some(v) if v nonEmpty => v
      case Some(v) => Logger.info("property at [$key] was defined but EMPTY defaulting the value")
      "should-be-not-empty"
      //throw new IllegalArgumentException(s"property at [$key] was empty")
      case _ =>
        Logger.info("property at [$key] was NOT FOUND")
      //throw new IllegalArgumentException(s"property at [$key] was missing")
        "should-be-not-empty"
    }
  }

  val accessKey: String = required("aws.accessKey", configuration)
  val secret:    String = required("aws.secret", configuration)

  val gcmConfiguration: Option[GcmConfiguration] = configuration getConfig "aws.platform.gcm" map GcmConfiguration

  case class GcmConfiguration(gcmConfig:Configuration) {

    val registrationId:  String = required("registrationId", gcmConfig)
    val serverApiKey:    String = required("serverApiKey", gcmConfig)
    val applicationName: String = required("applicationName", gcmConfig)
  }
}
