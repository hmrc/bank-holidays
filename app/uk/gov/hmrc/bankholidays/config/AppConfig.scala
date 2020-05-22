/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.bankholidays.config

import java.util.Base64

import javax.inject.{Inject, Singleton}
import play.api.Mode.Mode
import play.api.{Configuration, Environment}
import uk.gov.hmrc.play.config.ServicesConfig

@Singleton
class AppConfig @Inject()(
                           val runModeConfiguration: Configuration,
                           environment: Environment
                         ) extends ServicesConfig {

  lazy val assetsPrefix: String = loadConfig(s"assets.url") + loadConfig(s"assets.version")
  lazy val analyticsToken: String = loadConfig(s"google-analytics.token")
  lazy val analyticsHost: String = loadConfig(s"google-analytics.host")
  lazy val bankHolidaysUrl: String = loadConfig("bank-holidays-url")
  lazy val proxy: Option[ProxyConfiguration] = if (getBoolean("proxy.proxyRequiredForThisEnvironment")) {
    Some(ProxyConfiguration(
      getString("proxy.username"),
      new String(Base64.getDecoder.decode(getString("proxy.password"))),
      getString("proxy.protocol"),
      getString("proxy.host"),
      getInt("proxy.port")
    ))
  } else None

  override def mode: Mode = environment.mode

  def loadConfig(key: String): String =
    runModeConfiguration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

}