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
import uk.gov.hmrc.snsclient.config.ConfigKeys._
import uk.gov.hmrc.snsclient.config.RequiredKeys

import scala.language.postfixOps

@Singleton
class SnsConfiguration @Inject()(val configuration: Configuration) extends AwsConfiguration {

  val gcmConfiguration: Option[GcmConfiguration] = GcmConfiguration from configuration
  val platforms: Seq[Option[GcmConfiguration]] = Seq(gcmConfiguration)
}

case class GcmConfiguration(osName: String, apiKey: String, applicationArn: String)

object GcmConfiguration extends RequiredKeys {
  def from(gcmConfig: Configuration): Option[GcmConfiguration] =
    gcmConfig.getConfig(androidConfigurationKey).map( _ =>
      GcmConfiguration(
        requiredString(gcmOsKey, gcmConfig),
        requiredEncryptedString(gcmApiKey, gcmConfig),
        requiredString(gcmApplicaitonArnKey, gcmConfig)
      )
    )
}