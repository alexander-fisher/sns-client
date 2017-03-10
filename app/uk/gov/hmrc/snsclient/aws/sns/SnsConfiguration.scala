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

import play.api.Configuration
import uk.gov.hmrc.snsclient.aws.AwsConfiguration

import scala.language.postfixOps


@Singleton
class SnsConfiguration @Inject()(val configuration:Configuration) extends AwsConfiguration {

  private val AndroidConfig = "aws.platform.gcm"

  val gcmConfiguration : Option[GcmConfiguration] = configuration getConfig AndroidConfig map GcmConfiguration

  case class GcmConfiguration(gcmConfig:Configuration) {
    val osName:          String = required("osName", gcmConfig)
    val apiKey:          String = required("apiKey", gcmConfig)
    val applicationArn:  String = required("applicationArn", gcmConfig)
  }

  val platformsApplicationsOsMap: Map[String, String] = {
    Seq(gcmConfiguration).flatten.map(platform => platform.osName -> platform.applicationArn) toMap
  }
}