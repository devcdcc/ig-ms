package controllers

import com.github.devcdcc.services.queue.{Message, MessageValueConverter, Publisher, SimpleStringMessageValueConverter}
import controllers.authentication.AccessTokenHelper
import controllers.helps.PublisherHelper
import javax.inject._
import play.api.Configuration
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.util.Failure

@Singleton
class UserScrapperController @Inject()(
    config: Configuration,
    cc: ControllerComponents,
    publisher: Publisher[String, String])
    extends AbstractController(cc)
    with AccessTokenHelper
    with PublisherHelper {

  implicit private lazy val executionContext: ExecutionContext = defaultExecutionContext
  implicit val simpleStringMessageValueConverter: MessageValueConverter[String, String] =
    new SimpleStringMessageValueConverter

  //TODO: Set message value on sendAsync method call.
  def scrapUser(userId: String) = Action.async { implicit request: Request[AnyContent] =>
    authenticatedPrivateSiteIdAsync { authenticatedUser =>
      publisher
        .sendAsync(Message(userScrapperTopic, ""))
        .map(message => Ok(authenticatedUser.toString))
        .recover {
          case fail => InternalServerError(authenticatedUser.toString)
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
