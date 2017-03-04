package uk.gov.hmrc.support


import org.scalatest.Suite
import org.scalatestplus.play.PlaySpec

import scala.concurrent.ExecutionContext

trait ControllerSpec
  extends PlaySpec
    with ResettingMockitoSugar
    with AkkaMaterializerSpec
    with OneAppPerSuiteWithoutMetrics { this: Suite =>


  implicit val ctx: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
}
