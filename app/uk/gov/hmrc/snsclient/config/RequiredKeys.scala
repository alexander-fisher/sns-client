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

import scala.language.postfixOps

trait RequiredKeys {

  def requiredString(key:String, config:Configuration): String = {
    config getString key match {
      case Some(v) if v nonEmpty => v
      case Some(v) => throw new IllegalArgumentException(s"property at [$key] was empty")
      case _ => throw new IllegalArgumentException(s"property at [$key] was missing")
    }
  }

  def requiredInt(key:String, config:Configuration): Int = {
    try requiredString(key, config) toInt catch {
      case _: NumberFormatException => throw new IllegalArgumentException(s"property at [$key] was not an integer")
    }
  }

  def requiredBoolean(key:String, config:Configuration): Boolean = {
    config getBoolean key getOrElse(throw new IllegalArgumentException(s"property at [$key] was missing"))
  }
}
