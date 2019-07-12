package controllers

import com.github.devcdcc.services.queue.{CirceToStringMessageValueConverter, Message, MessageValueConverter, Publisher}
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.Configuration
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import services.ig.wrapper._
import services.util.RandomGenerator
import AuthenticationHelper._
import scala.concurrent.Future

class UserScrapperControllerSpec extends PlaySpec with MockitoSugar {
  var controllerComponents         = mock[ControllerComponents]
  var publisher                    = mock[Publisher[String, String]]
  val configuration: Configuration = mock[Configuration]
  val random: RandomGenerator      = mock[RandomGenerator]

  // subject
  val subject =
    new UserScrapperController(
      config = configuration,
      randomService = random,
      cc = Helpers.stubControllerComponents(),
      publisher = publisher
    )
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
    "return json when everything is okay" in {
      //given
      val userId = "123123"
      val url    = s"/user/$userId"
      implicit val simpleStringMessageValueConverter: MessageValueConverter[Json, String] =
        new CirceToStringMessageValueConverter
      val user                                   = UserRequest(userId = userId, id = Option(random.generate()))
      val message: Message[String, Json, String] = Message(subject.userScrapperTopic, user.asJson)
      val expected                               = user.asJson.noSpaces

      //when
      user.id.foreach(id => when(random.generate()) thenReturn id)
      when(publisher.sendAsync(message)) thenReturn Future.successful(message)

      //then
      val result = subject.scrapUser(userId).apply(FakeRequest(POST, url).addAuthentication(userId))
      contentAsString(result) mustBe expected
    }
  }
}
