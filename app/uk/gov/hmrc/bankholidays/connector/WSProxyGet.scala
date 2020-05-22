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
import com.typesafe.config.Config
import javax.inject.Inject
import play.api.libs.ws.{WSClient, WSProxyServer, WSRequest}
import play.api.{Configuration, Logger}
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet}
import uk.gov.hmrc.play.http.ws.{WSGet, WSProxyConfiguration}

class WSProxyGet @Inject()(
                            val config: Configuration,
                            val ws: WSClient,
                            override val actorSystem: ActorSystem
                          ) extends HttpGet with WSGet {

  override val hooks: Seq[HttpHook] = NoneRequired

  override def buildRequest[A](url: String, headers: Seq[(String, String)] = Seq.empty)(implicit hc: HeaderCarrier): WSRequest = {
    wsProxyServer match {
      case Some(proxy) =>
        Logger.info(s"Using Proxy [" +
          s"protocol:${proxy.protocol.get}," +
          s"port:${proxy.port}," +
          s"host:${proxy.host}," +
          s"user:${proxy.principal.get}," +
          s"password:${proxy.password.map(_.substring(0, 2)).get}" +
          s"]")
        wsClient.url(url).withProxyServer(proxy)
      case None =>
        wsClient.url(url)
    }
  }

  private def wsProxyServer: Option[WSProxyServer] = WSProxyConfiguration("proxy", config)

  override protected def configuration: Option[Config] = Some(config.underlying)

  override def wsClient: WSClient = ws
}
