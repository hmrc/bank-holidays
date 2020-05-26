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

package uk.gov.hmrc.bankholidays.connector

import akka.actor.ActorSystem
import play.api.Configuration
import play.api.libs.ws.{DefaultWSProxyServer, WSClient, WSProxyServer}
import uk.gov.hmrc.bankholidays.BaseTestSpec

class WSProxyGetSpec extends BaseTestSpec {

  private val config = app.injector.instanceOf[Configuration]
  private val ws = mock[WSClient]
  private val actor = app.injector.instanceOf[ActorSystem]

  private def wsProxyServer(
                             protocol: Option[String] = Some("protocol"),
                             host: String = "host",
                             port: Int = 1234,
                             principal: Option[String] = Some("principal"),
                             password: Option[String] = Some("pass")
                           ): WSProxyServer = DefaultWSProxyServer(
    protocol = protocol,
    host = host,
    port = port,
    principal = principal,
    password = password
  )

  private def controller(): WSProxyGet = {
    new WSProxyGet(config, ws, actor)
  }

  "WSProxyGet" should {

    "should not throw any exception while printProxyConfig fully packed" in {
      controller().printProxyConfig(wsProxyServer())
    }

    "should not throw any exception while printProxyConfig without protocol" in {
      controller().printProxyConfig(wsProxyServer(protocol = None))
    }

    "should not throw any exception while printProxyConfig without principal" in {
      controller().printProxyConfig(wsProxyServer(principal = None))
    }

    "should not throw any exception while printProxyConfig without password" in {
      controller().printProxyConfig(wsProxyServer(password = None))
    }

    "should not throw any exception while printProxyConfig with 0 char password" in {
      controller().printProxyConfig(wsProxyServer(password = Some("")))
    }

    "should not throw any exception while printProxyConfig with 2 char password" in {
      controller().printProxyConfig(wsProxyServer(password = Some("ab")))
    }

    "should not throw any exception while printProxyConfig with 3 char password" in {
      controller().printProxyConfig(wsProxyServer(password = Some("abc")))
    }

  }
}
