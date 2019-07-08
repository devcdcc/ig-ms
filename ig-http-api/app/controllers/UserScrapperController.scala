package controllers

import com.github.devcdcc.services.queue.Publisher
import controllers.authentication.AccessTokenHelper
import javax.inject._
import play.api.inject.ApplicationLifecycle
import play.api.mvc._

import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class UserScrapperController @Inject()(cc: ControllerComponents, publisher: Publisher[String, String])
    extends AbstractController(cc)
    with AccessTokenHelper {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Unauthorized("")
    Ok(views.html.index())
  }

  def scrapUser(userId: String) = Action.async { implicit request: Request[AnyContent] =>
    authenticatedPrivateSiteIdAsync { authenticatedUser =>
      Future.successful(Ok(authenticatedUser.toString))
    }

  }

  def scrapMedia(userId: String) = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

  def scrapFollowing(userId: String) = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

  def scrapFollowers(userId: String) = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

}
