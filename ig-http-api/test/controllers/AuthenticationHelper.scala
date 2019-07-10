package controllers

import controllers.authentication.AccessTokenHelper
import play.api.mvc.AnyContent
import play.api.test.FakeRequest

object AuthenticationHelper {
  implicit class FakeRequestHelper(fakeRequest: FakeRequest[AnyContent]) extends AccessTokenHelper {

    def addAuthentication(user: String = "12345678"): FakeRequest[AnyContent] =
      fakeRequest.withHeaders((headerName, s"${user}_private"))
  }
}
