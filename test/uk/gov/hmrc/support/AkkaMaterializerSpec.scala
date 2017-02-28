package uk.gov.hmrc.support

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.{BeforeAndAfterAll, Suite}

import scala.concurrent.Await
import scala.concurrent.duration._

trait AkkaMaterializerSpec extends BeforeAndAfterAll { this: Suite =>

  implicit lazy val actorSystem = ActorSystem()
  implicit lazy val materializer = ActorMaterializer()

  override protected def afterAll() = {
    super.afterAll()
    Await.result(actorSystem.terminate(), 1 second)
  }
}
