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
import com.github.tomakehurst.wiremock.client.WireMock._
import org.mockito.BDDMockito.given
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.bankholidays.config.{AppConfig, ProxyConfiguration}
import uk.gov.hmrc.bankholidays.connector.WSProxyGet
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}


class IndexControllerControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication with BeforeAndAfterEach {

  private val wirePort = 20001
  private val wireHost = "localhost"
  private val host: String = s"http://$wireHost:$wirePort"
  private val wireMockServer = new WireMockServer(wirePort)

  private val fakeRequest = FakeRequest("GET", "/")
  private val wsClient: WSProxyGet = fakeApplication.injector.instanceOf[WSProxyGet]
  private val appConfig = mock[AppConfig]
  private implicit val mat: Materializer = fakeApplication.materializer
  private def controller = new IndexController(wsClient, appConfig)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    wireMockServer.start()
    configureFor(wireHost, wirePort)
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    wireMockServer.resetAll()
    wireMockServer.stop()
  }

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

      val result: Result = await(controller.get(fakeRequest))
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("application/json")
      bodyOf(result) shouldBe "{}"
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

      val result: Result = await(controller.get(fakeRequest))
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      bodyOf(result) shouldBe "xyz"
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

      val result: Result = await(controller.get(fakeRequest))
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("application/json")
      bodyOf(result) shouldBe "[]"
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

      val result: Result = await(controller.get(fakeRequest))
      status(result) shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      bodyOf(result) shouldBe "abc"
    }

  }
}