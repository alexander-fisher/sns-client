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

package uk.gov.hmrc.snsclient

import javax.inject.{Inject, Singleton}

import com.google.inject.Provider
import uk.gov.hmrc.snsclient.aws.sns.SnsConfiguration

@Singleton
class PlatformArnProvider @Inject()(snsConfig:SnsConfiguration) extends Provider[Map[String, String]]{

  override def get(): Map[String, String] = {
    snsConfig
      .platforms
      .flatten
      .map(platform => platform.osName -> platform.applicationArn) toMap
  }
}
