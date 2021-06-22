/*
 * Copyright 2021 HM Revenue & Customs
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
import com.typesafe.config.Config
import javax.inject.Inject
import play.api.libs.ws.{WSClient, WSProxyServer, WSRequest}
import play.api.{Configuration, Logging}
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.http.HttpGet
import uk.gov.hmrc.play.http.ws.{WSGet, WSProxyConfiguration}

class WSProxyGet @Inject()(
                            val config: Configuration,
                            val ws: WSClient,
                            override val actorSystem: ActorSystem
                          ) extends HttpGet with WSGet with Logging {

  override val hooks: Seq[HttpHook] = NoneRequired

  override def buildRequest[A](url: String, headers: Seq[(String, String)] = Seq.empty): WSRequest = {
    wsProxyServer match {
      case Some(proxy) =>
        printProxyConfig(proxy)
        wsClient.url(url).withProxyServer(proxy)
      case None =>
        wsClient.url(url)
    }
  }

  def printProxyConfig(proxy: WSProxyServer): Unit = {
    logger.info(s"Using Proxy [" +
      s"protocol:${proxy.protocol.getOrElse("")}," +
      s"port:${proxy.port}," +
      s"host:${proxy.host}," +
      s"user:${proxy.principal.getOrElse("")}," +
      s"password:${getProxyPass(proxy)}" +
      s"]")
  }

  private def getProxyPass(proxy: WSProxyServer): String = {
    val pass = proxy.password.getOrElse("")
    if (pass.length > 2) {
      pass.substring(0, 2)
    } else {
      ""
    }
  }

  private def wsProxyServer: Option[WSProxyServer] = WSProxyConfiguration("proxy", config)

  override def wsClient: WSClient = ws

  override protected def configuration: Config = config.underlying
}
