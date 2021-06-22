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

package uk.gov.hmrc.bankholidays.controllers

import akka.stream.Materializer
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import com.github.tomakehurst.wiremock.client.WireMock._
import org.mockito.BDDMockito.given
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status
import play.api.mvc.{MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.Helpers.{status => helperStatus}
import uk.gov.hmrc.bankholidays.config.{AppConfig, ProxyConfiguration}
import uk.gov.hmrc.bankholidays.connector.WSProxyGet
import org.scalatest.{Matchers, OptionValues, WordSpecLike}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

import scala.concurrent.Future


class IndexControllerControllerSpec extends WordSpecLike with Matchers with OptionValues with MockitoSugar with GuiceOneServerPerSuite
  with BeforeAndAfterEach with BeforeAndAfterAll {

  private val wirePort = 20001
  private val wireHost = "localhost"
  private val host: String = s"http://$wireHost:$wirePort"
  private val wireMockServer = new WireMockServer(wirePort)

  private val fakeRequest = FakeRequest("GET", "/")
  private val wsClient: WSProxyGet = app.injector.instanceOf[WSProxyGet]
  private val appConfig = mock[AppConfig]
  private implicit val mat: Materializer = app.injector.instanceOf[Materializer]
  private val cc: MessagesControllerComponents = app.injector.instanceOf[MessagesControllerComponents]

  override def beforeAll(): Unit = {
    super.beforeAll()

    wireMockServer.start()
    configureFor(wireHost, wirePort)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    wireMockServer.stop()
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    wireMockServer.resetAll()
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
  }

  private def controller = new IndexController(wsClient, appConfig, cc)

  "GET /" should {

    "return 200 without proxy" in {
      given(appConfig.bankHolidaysUrl) willReturn host + "/bank-holidays.json"
      given(appConfig.proxy) willReturn None

      stubFor(
        get("/bank-holidays.json")
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody("{}")
          )
      )

      val result: Future[Result] = controller.get(fakeRequest)
      helperStatus(result) shouldBe Status.OK
      contentType(result) shouldBe Some("application/json")
      contentAsString(result) shouldBe "{}"
    }

    "return 200 with non-json without proxy" in {
      given(appConfig.bankHolidaysUrl) willReturn host + "/bank-holidays.json"
      given(appConfig.proxy) willReturn None

      stubFor(
        get("/bank-holidays.json")
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody("xyz")
          )
      )

      val result: Future[Result] = controller.get(fakeRequest)
      helperStatus(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      contentAsString(result) shouldBe "xyz"
    }

    "return 200 with proxy" in {
      given(appConfig.bankHolidaysUrl) willReturn host + "/bank-holidays.json"
      given(appConfig.proxy) willReturn Some(ProxyConfiguration("user", "password", "http", wireHost, wirePort))

      stubFor(
        get("/bank-holidays.json")
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody("[]")
          )
      )

      val result: Future[Result] = controller.get(fakeRequest)
      helperStatus(result) shouldBe Status.OK
      contentType(result) shouldBe Some("application/json")
      contentAsString(result) shouldBe "[]"
    }

    "return 200 with non-json with proxy" in {
      given(appConfig.bankHolidaysUrl) willReturn host + "/bank-holidays.json"
      given(appConfig.proxy) willReturn Some(ProxyConfiguration("user", "password", "http", wireHost, wirePort))

      stubFor(
        get("/bank-holidays.json")
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody("abc")
          )
      )

      val result: Future[Result] = controller.get(fakeRequest)
      helperStatus(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      contentAsString(result) shouldBe "abc"
    }

  }
}