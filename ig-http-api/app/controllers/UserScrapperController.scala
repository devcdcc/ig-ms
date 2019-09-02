package controllers

import com.github.devcdcc.domain.{MediaRequest, QueueRequest, UserRequest}
import com.github.devcdcc.services.queue.Publisher
import controllers.authentication.AccessTokenHelper
import controllers.helpers.PublisherHelper
import javax.inject._
import play.api.Configuration
import play.api.mvc._
import services.random.RandomGenerator

@Singleton
class UserScrapperController @Inject()(
    config: Configuration,
    randomService: RandomGenerator,
    cc: ControllerComponents,
    publisher: Publisher[String, String])
    extends PublisherHelper(config, cc, randomService, publisher)
    with AccessTokenHelper {
//  implicit val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  private def generalScrapper(userId: String)(implicit user: QueueRequest): Action[AnyContent] = Action.async {
    implicit request =>
      authenticatedPrivateSiteIdAsync(_ => basicRequestMaker(userId, request.path))
  }

  def scrapUser(userId: String): Action[AnyContent] = {
    implicit val user: QueueRequest = UserRequest(userId = userId, requestId = Option(randomService.generate()))
    generalScrapper(userId)
  }

  def scrapMedia(userId: String): Action[AnyContent] = {
    implicit val user: QueueRequest = MediaRequest(userId = userId, requestId = Option(randomService.generate()))
    generalScrapper(userId)
  }

  def scrapFollowing(userId: String): Action[AnyContent] = {
    implicit val user: QueueRequest = UserRequest(userId = userId, requestId = Option(randomService.generate()))
    generalScrapper(userId)
  }

  def scrapFollowers(userId: String): Action[AnyContent] = {
    implicit val user: QueueRequest = UserRequest(userId = userId, requestId = Option(randomService.generate()))
    generalScrapper(userId)
  }
}
