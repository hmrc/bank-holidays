import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-play-25"          % "4.10.0",
    "uk.gov.hmrc"             %% "govuk-template"             % "5.31.0-play-25",
    "uk.gov.hmrc"             %% "play-ui"                    % "7.40.0-play-25"
  )

  val scope = "test, it"

  val test = Seq(
    "com.github.tomakehurst"  %  "wiremock"                 % "2.22.0"        % scope,
    "com.typesafe.play"       %% "play-test"                % current         % scope,
    "org.mockito"             %  "mockito-core"             % "2.26.0"        % scope,
    "org.pegdown"             %  "pegdown"                  % "1.6.0"         % scope,
    "org.scalatest"           %% "scalatest"                % "3.0.4"         % scope,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "2.0.1"         % scope,
    "uk.gov.hmrc"             %% "hmrctest"                 % "3.8.0-play-25" % scope,
    "uk.gov.hmrc"             %% "http-verbs-test"          % "1.6.0-play-25" % scope,
    "uk.gov.hmrc"             %% "service-integration-test" % "0.6.0-play-25" % scope
  )

}
