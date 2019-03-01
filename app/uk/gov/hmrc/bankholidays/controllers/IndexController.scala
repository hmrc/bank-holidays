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

package uk.gov.hmrc.bankholidays.controllers

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSProxyServer, WSResponse}
import play.api.mvc._
import uk.gov.hmrc.bankholidays.config.AppConfig
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

@Singleton
class IndexController @Inject()(client: WSClient, appConfig: AppConfig) extends FrontendController {

  private val proxyServer: Option[WSProxyServer] = appConfig.proxy.map { proxy =>
    new WSProxyServer {
      override def host: String = proxy.host

      override def port: Int = proxy.port

      override def protocol: Option[String] = Some(proxy.protocol)

      override def principal: Option[String] = Some(proxy.user)

      override def password: Option[String] = Some(proxy.password)

      override def ntlmDomain: Option[String] = None

      override def encoding: Option[String] = Some("UTF-8")

      override def nonProxyHosts: Option[Seq[String]] = None
    }
  }

  private val url: String = appConfig.bankHolidaysUrl
  Logger.info(s"Proxying $url")

  def get: Action[AnyContent] = Action.async { implicit request =>

    val response: Future[WSResponse] = proxyServer match {
      case Some(proxy) => client.url(url).withProxyServer(proxy).get()
      case None => client.url(url).get()
    }

    response.map(_.body).map { body =>
      Try(Json.parse(body))
        .map(Ok(_))
        .getOrElse(Ok(body).as("text/html"))
    }
  }

}
