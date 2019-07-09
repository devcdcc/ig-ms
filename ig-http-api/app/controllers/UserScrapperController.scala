package controllers

import com.github.devcdcc.services.queue.{Message, MessageValueConverter, Publisher, SimpleStringMessageValueConverter}
import controllers.authentication.AccessTokenHelper
import controllers.helps.PublisherHelper
import javax.inject._
import play.api.Configuration
import play.api.mvc._
import services.ig.wrapper.User

import scala.concurrent.ExecutionContext
import io.circe.generic.auto._, io.circe.syntax._
import services.util.RandomGenerator

@Singleton
class UserScrapperController @Inject()(
    val config: Configuration,
    cc: ControllerComponents,
    publisher: Publisher[String, String])
    extends AbstractController(cc)
    with AccessTokenHelper
    with PublisherHelper
    with play.api.libs.circe.Circe {
  implicit private lazy val executionContext: ExecutionContext = defaultExecutionContext
  implicit val simpleStringMessageValueConverter: MessageValueConverter[String, String] =
    new SimpleStringMessageValueConverter

  //TODO: Set message value on sendAsync method call.
  def scrapUser(userId: String) = Action.async { implicit request: Request[AnyContent] =>
    authenticatedPrivateSiteIdAsync { authenticatedUser =>
      publisher
        .sendAsync(
          Message(userScrapperTopic, User(userId = userId, id = Option(RandomGenerator.generate())).asJson.noSpaces)
        )
        .map(message => Ok(message.asJson))
        .recover {
          case fail =>
            InternalServerError(fail.getMessage)
        }
    }

  }

  def scrapMedia(userId: String) = Action { implicit request: Request[AnyContent] =>
    ???
  }

  def scrapFollowing(userId: String) = Action { implicit request: Request[AnyContent] =>
    ???
  }

  def scrapFollowers(userId: String) = Action { implicit request: Request[AnyContent] =>
    ???
  }

}
