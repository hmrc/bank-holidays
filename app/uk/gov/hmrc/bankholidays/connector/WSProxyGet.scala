/*
 * Copyright 2019 HM Revenue & Customs
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

class WSProxyGet @Inject()(val config: Configuration,
                           ws: WSClient,
                           override val actorSystem: ActorSystem) extends HttpGet with WSGet {

  private def wsProxyServer: Option[WSProxyServer] = WSProxyConfiguration("proxy")
  override protected def configuration: Option[Config] = Some(config.underlying)
  override val hooks: Seq[HttpHook] = NoneRequired

  override def buildRequest[A](url: String)(implicit hc: HeaderCarrier): WSRequest = {
    wsProxyServer match {
      case Some(proxy) =>
        Logger.info(s"Using Proxy [protocol:${proxy.protocol.get},port:${proxy.port},host:${proxy.host},user:${proxy.principal.get},password:${proxy.password.map(_.substring(0, 2)).get}]")
        ws.url(url).withProxyServer(proxy)
      case None => ws.url(url)
    }
  }
}
