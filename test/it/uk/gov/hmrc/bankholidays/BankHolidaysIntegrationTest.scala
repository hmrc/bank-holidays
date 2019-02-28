package uk.gov.hmrc.bankholidays

import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import uk.gov.hmrc.play.test.UnitSpec

class BankHolidaysIntegrationTest extends UnitSpec with GuiceOneServerPerSuite  {

  "Bank Holidays" should {
    "Proxy" in {
      val wsClient = app.injector.instanceOf[WSClient]

      val response = await(wsClient.url(s"http://localhost:$port/bank-holidays").get)
      response.status shouldBe 200
    }
  }
}
