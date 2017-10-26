import sbt._

object AppDependencies {
  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val bootstrap = "6.11.0"
  private val awsSns = "1.11.97"
  private val playHmrcApiVersion = "2.1.0"


  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % bootstrap,
    "uk.gov.hmrc" %% "play-hmrc-api" % playHmrcApiVersion,
    "com.amazonaws" % "aws-java-sdk-sns" % awsSns
  )

  private val scalaTestPlus = "2.0.1"
  private val hmrcTest = "3.0.0"
  private val scalactic = "3.0.4"
  private val mockitoCore = "2.11.0"

  def test(scope: String = "test,it") = {
    Seq(
      "uk.gov.hmrc" %% "hmrctest" % hmrcTest % scope,
      "org.scalactic" %% "scalactic" % scalactic % scope,
      "org.scalatest" %% "scalatest" % scalactic % scope,
      "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlus % scope,
      "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
      "org.mockito" % "mockito-core" % mockitoCore % scope
    )
  }

  def apply() = compile ++ test()
}
