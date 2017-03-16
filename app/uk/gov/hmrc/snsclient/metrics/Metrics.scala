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

package uk.gov.hmrc.snsclient.metrics

import javax.inject.Singleton

import com.codahale.metrics.MetricRegistry
import com.google.inject.ImplementedBy
import uk.gov.hmrc.play.graphite.MicroserviceMetrics


@ImplementedBy(classOf[Metrics])
trait MetricsApi {
  def registry: com.codahale.metrics.MetricRegistry

  def batchEndpointCreationSuccess()
  def batchEndpointCreationFailure()
  def endpointCreationFailure()
  def endpointCreationSuccess()
  def unknownOsFailure()

  def batchPublicationSuccess()
  def batchPublicationFailure()
  def endpointDisabledFailure()
  def publishSuccess()
  def publishFailure()
}

@Singleton
class Metrics extends MetricsApi with MicroserviceMetrics {

  lazy val registry: MetricRegistry = metrics.defaultRegistry
  val successful = s"sns-client.successful"
  val failed = s"sns-client.failed"

  override def batchEndpointCreationSuccess(): Unit = registry.meter(s"$successful.endpoint-creation").mark(1L)
  override def batchEndpointCreationFailure(): Unit = registry.meter(s"$failed.batch-endpoints").mark(1L)
  override def endpointCreationSuccess(): Unit = registry.meter(s"$successful.batch-endpoints").mark(1L)
  override def endpointCreationFailure(): Unit = registry.meter(s"$failed.endpoint-creation").mark(1L)
  override def unknownOsFailure(): Unit = registry.meter(s"$failed.unknown-os").mark(1L)

  override def publishSuccess(): Unit = registry.meter(s"$successful.publish").mark(1L)
  override def publishFailure(): Unit = registry.meter(s"$failed.publish").mark(1L)
  override def endpointDisabledFailure(): Unit = registry.meter(s"$failed.publish").mark(1L)
  override def batchPublicationSuccess(): Unit = registry.meter(s"$successful.batch-publish").mark(1L)
  override def batchPublicationFailure(): Unit = registry.meter(s"$failed.batch-publish").mark(1L)
}
