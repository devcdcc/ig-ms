package controllers

import com.github.devcdcc.services.queue.{CirceToStringMessageValueConverter, Message, MessageValueConverter, Publisher}
import controllers.authentication.AccessTokenHelper
import controllers.helps.{PublisherHelper, TopicsHelper}
import io.circe.{Json, Printer}
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
//  implicit val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  def generalScrapper(userId: String, destinationTopic: String) = Action.async { implicit request =>
    authenticatedPrivateSiteIdAsync(_ => basicRequestMaker(userId, destinationTopic))
  }

  def scrapUser(userId: String) = generalScrapper(userId, userScrapperTopic)

  def scrapMedia(userId: String) = generalScrapper(userId, userMediaScrapperTopic)

  def scrapFollowing(userId: String) = generalScrapper(userId, userFollowingScrapperTopic)

  def scrapFollowers(userId: String) = generalScrapper(userId, userFollowersScrapperTopic)

}
