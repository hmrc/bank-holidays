import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-26"       % "5.3.0",
    "uk.gov.hmrc"       %% "govuk-template"                   % "5.68.0-play-26",
    "uk.gov.hmrc"       %% "play-ui"                          % "9.5.0-play-26"
  )

  val scope = "test"

  val test = Seq(
    "com.github.tomakehurst"     % "wiremock-jre8"          % "2.27.2"            % scope,
    "com.typesafe.play"         %% "play-test"              % PlayVersion.current % scope,
    "org.mockito"                % "mockito-core"           % "3.11.0"            % scope,
    "org.pegdown"                % "pegdown"                % "1.6.0"             % scope,
    "org.jsoup"                  % "jsoup"                  % "1.13.1"            % scope,
    "org.scalacheck"            %% "scalacheck"             % "1.15.4"            % scope,
    "org.scalatest"             %% "scalatest"              % "3.0.9"             % scope,
    "org.scalatestplus.play"    %% "scalatestplus-play"     % "3.1.3"             % scope,
    "uk.gov.hmrc"               %% "hmrctest"               % "3.10.0-play-26"     % scope
  )

}
