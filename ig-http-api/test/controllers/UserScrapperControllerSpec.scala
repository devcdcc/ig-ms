package controllers

import com.github.devcdcc.services.queue.Publisher
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

class UserScrapperControllerSpec extends PlaySpec with MockitoSugar {
  var controllerComponents = mock[ControllerComponents]
  var publisher            = mock[Publisher[String, String]]
  // subject
  val subject = new UserScrapperController(Helpers.stubControllerComponents(), publisher)
  "For scrapUser" should {
    "return unauthorized response when is not authenticated" in {
      //given
      val userId   = "123123"
      val url      = s"/user/$userId"
      val expected = UNAUTHORIZED

      //when
      val result = subject.scrapUser(userId).apply(FakeRequest(POST, url))

      //then
      status(result) mustBe expected
    }
  }
}
