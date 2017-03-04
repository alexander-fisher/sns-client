package uk.gov.hmrc.support

import org.mockito.Mockito
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatest.mock.MockitoSugar

import scala.reflect.Manifest

trait ResettingMockitoSugar extends MockitoSugar with BeforeAndAfterEach {
  this: Suite =>

  var mocksToReset = Seq.empty[Any]

  def resettingMock[T <: AnyRef](implicit manifest: Manifest[T]): T = {
    val m = mock[T](manifest)
    mocksToReset = mocksToReset :+ m
    m
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset(mocksToReset: _*)
  }
}