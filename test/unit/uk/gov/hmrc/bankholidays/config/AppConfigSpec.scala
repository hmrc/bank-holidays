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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.{Configuration, Environment}
import uk.gov.hmrc.bankholidays.BaseTestSpec

class AppConfigSpec extends BaseTestSpec {

  private val env: Environment = app.injector.instanceOf[Environment]

  private def appConfig(pairs: (String, String)*): AppConfig = {
    new AppConfig(Configuration.from(pairs.map(e => e._1 -> e._2).toMap), env)
  }

  "Build assets prefix" in {
    appConfig(
      "assets.url" -> "http://localhost:9032/assets/",
      "assets.version" -> "4.5.0"
    ).assetsPrefix shouldBe "http://localhost:9032/assets/4.5.0"
  }

  "Build analytics token" in {
    appConfig("google-analytics.token" -> "N/A").analyticsToken shouldBe "N/A"
  }

  "Build analytics host" in {
    appConfig("google-analytics.host" -> "auto").analyticsHost shouldBe "auto"
  }

  "Build bank holidays url" in {
    appConfig("bank-holidays-url" -> "x").bankHolidaysUrl shouldBe "x"
  }

  "Build proxy - None" in {
    appConfig("proxy.proxyRequiredForThisEnvironment" -> "false").proxy shouldBe None
  }

  "Build proxy - Some" in {
    appConfig(
      "proxy.proxyRequiredForThisEnvironment" -> "true",
      "proxy.host" -> "host",
      "proxy.port" -> "123",
      "proxy.protocol" -> "protocol",
      "proxy.username" -> "username",
      "proxy.password" -> "cGFzc3dvcmQ="
    ).proxy shouldBe Some(ProxyConfiguration("username", "password", "protocol", "host", 123))
  }

  "mode is correct" in {
    appConfig(
      "proxy.proxyRequiredForThisEnvironment" -> "true",
      "proxy.host" -> "host",
      "proxy.port" -> "123",
      "proxy.protocol" -> "protocol",
      "proxy.username" -> "username",
      "proxy.password" -> "cGFzc3dvcmQ="
    ).mode shouldBe env.mode
  }

  "missing key in config" in {
    val config = mock[Configuration]
    val appConfig = new AppConfig(config, env)

    when(config.getString(any(), any())).thenReturn(None)

    assertThrows[Exception](appConfig.loadConfig("random.invalid.key.test"))
  }

}