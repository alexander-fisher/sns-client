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

object ConfigKeys {
  val platformConfigurationKey = "aws.platform.PLATFORM"
  val apiKey = s"$platformConfigurationKey.apiKey"
  val applicationArnKey = s"$platformConfigurationKey.applicationArn"
  val awsAccessKey = "aws.accessKey"
  val awsSecretKey = "aws.secret"
  val awsRegionKey = "aws.region"
  val awsStubbingKey = "aws.stubbing"
  val awsRegionOverrideKey = "aws.regionOverrideForStubbing"
  val proxyKey = "proxy"
  val proxyUserNameKey = "username"
  val proxyPasswordKey = "password"
  val proxyHostKey = "host"
  val proxyPortKey = "port"
}
