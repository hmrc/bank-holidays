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

package uk.gov.hmrc.bankholidays.views

import play.twirl.api.Html
import uk.gov.hmrc.bankholidays.BaseTestSpec
import uk.gov.hmrc.bankholidays.views.html.error_template

class ErrorTemplateSpec extends BaseTestSpec {

  val error_template: error_template = app.injector.instanceOf[error_template]

  def view(pageTitle: String, heading: String, message: String): Html =
    error_template(pageTitle, heading, message)


  "should render title in error template" in {
    val title = "custom title"
    val heading = "custom heading"
    val message = "custom message"
    val actual = view(title, heading, message).toString()
    val expected = "<title>custom title</title>"

    actual should include(expected)
  }

  "should render heading in error template" in {
    val title = "custom title"
    val heading = "custom heading"
    val message = "custom message"
    val actual = view(title, heading, message).toString()
    val expected = "<h1>custom heading</h1>"

    actual should include(expected)
  }

  "should render message in error template" in {
    val title = "custom title"
    val heading = "custom heading"
    val message = "custom message"
    val actual = view(title, heading, message).toString()
    val expected = "<p>custom message</p>"

    actual should include(expected)
  }

}
