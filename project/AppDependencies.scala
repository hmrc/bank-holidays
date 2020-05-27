import play.core.PlayVersion
import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-play-26"                % "1.7.0",
    "uk.gov.hmrc"       %% "govuk-template"                   % "5.54.0-play-26",
    "uk.gov.hmrc"       %% "play-ui"                          % "8.8.0-play-26"
  )

  val scope = "test"

  val test = Seq(
    "com.github.tomakehurst"     % "wiremock-jre8"          % "2.26.1"            % scope,
    "com.typesafe.play"         %% "play-test"              % PlayVersion.current % scope,
    "org.mockito"                % "mockito-core"           % "2.26.0"            % scope,
    "org.pegdown"                % "pegdown"                % "1.6.0"             % scope,
    "org.jsoup"                  % "jsoup"                  % "1.13.1"            % scope,
    "org.scalacheck"            %% "scalacheck"             % "1.14.3"            % scope,
    "org.scalatest"             %% "scalatest"              % "3.0.8"             % scope,
    "org.scalatestplus.play"    %% "scalatestplus-play"     % "3.1.3"             % scope,
    "uk.gov.hmrc"               %% "hmrctest"               % "3.9.0-play-26"     % scope
  )

}
