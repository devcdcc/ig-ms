package controllers

import com.github.devcdcc.services.queue.{CirceToStringMessageValueConverter, Message, MessageValueConverter, Publisher}
import controllers.authentication.AccessTokenHelper
import controllers.helps.{PublisherHelper, TopicsHelper}
import io.circe.Json
import javax.inject._
import play.api.{Configuration, Logging}
import play.api.mvc._
import services.ig.wrapper.UserRequest
import io.circe.generic.auto._
import io.circe.syntax._
import org.slf4j.MDC

import scala.concurrent.{ExecutionContext, Future}
import services.util.RandomGenerator
import play.api.libs.circe._

@Singleton
class UserScrapperController @Inject()(
    config: Configuration,
    randomService: RandomGenerator,
    cc: ControllerComponents,
    publisher: Publisher[String, String])
    extends PublisherHelper(config, cc, randomService, publisher)
    with AccessTokenHelper
    with TopicsHelper {
//  implicit val printer: Printer                                = Printer.noSpaces.copy(dropNullValues = true)

  def scrapUser(userId: String) = Action.async { implicit request =>
    val destinationTopic = userScrapperTopic
    authenticatedPrivateSiteIdAsync(_ => basicRequestMaker(userId, destinationTopic))
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
