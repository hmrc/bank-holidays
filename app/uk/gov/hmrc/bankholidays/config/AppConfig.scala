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
import play.api.Configuration

@Singleton
class AppConfig @Inject()(
                           val runModeConfiguration: Configuration
                         ) {

  lazy val assetsPrefix: String = getString(s"assets.url") + getString(s"assets.version")
  lazy val analyticsToken: String = getString(s"google-analytics.token")
  lazy val analyticsHost: String = getString(s"google-analytics.host")
  lazy val bankHolidaysUrl: String = getString("bank-holidays-url")
  lazy val proxy: Option[ProxyConfiguration] = if (getBoolean("proxy.proxyRequiredForThisEnvironment")) {
    Some(ProxyConfiguration(
      getString("proxy.username"),
      new String(Base64.getDecoder.decode(getString("proxy.password"))),
      getString("proxy.protocol"),
      getString("proxy.host"),
      getInt("proxy.port")
    ))
  } else None

  def getInt(key: String): Int = runModeConfiguration.getOptional[Int](key).getOrElse(configNotFoundError(key))

  def getString(key: String): String = runModeConfiguration.getOptional[String](key).getOrElse(configNotFoundError(key))

  def getBoolean(key: String): Boolean = runModeConfiguration.getOptional[Boolean](key).getOrElse(configNotFoundError(key))

  private def configNotFoundError(key: String) = throw new RuntimeException(s"Could not find config key '$key'")

}