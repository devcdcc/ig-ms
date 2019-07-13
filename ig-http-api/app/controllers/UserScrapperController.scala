package controllers

import com.github.devcdcc.services.queue.Publisher
import controllers.authentication.AccessTokenHelper
import controllers.helps.{PublisherHelper, TopicsHelper}
import javax.inject._
import play.api.Configuration
import play.api.mvc._

import services.util.RandomGenerator

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
