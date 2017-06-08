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

package uk.gov.hmrc.snsclient.config

import play.api.Configuration
import uk.gov.hmrc.crypto.{Crypted, CryptoGCMWithKeysFromConfig}
import uk.gov.hmrc.snsclient.config.ConfigKeys.awsEncryptionKey

trait RequiredKeys extends EncryptionHelper {

  def requiredString(key:String, config:Configuration): String = {
    config getString key match {
      case Some(v) if v nonEmpty => v
      case Some(v) => throw new IllegalArgumentException(s"property at [$key] was empty")
      case _ => throw new IllegalArgumentException(s"property at [$key] was missing")
    }
  }

  def requiredEncryptedString(key:String, config:Configuration): String = {
    plainTextValue(requiredString(key, config), config)
  }

  def requiredBoolean(key:String, config:Configuration): Boolean = {
    config getBoolean key getOrElse(throw new IllegalArgumentException(s"property at [$key] was missing"))
  }
}

trait EncryptionHelper {
  def plainTextValue(scrambledValue: String, configuration: Configuration): String = {
    val cipher = CryptoGCMWithKeysFromConfig(awsEncryptionKey, configuration)
    cipher.decrypt(Crypted(scrambledValue)).value
  }
}