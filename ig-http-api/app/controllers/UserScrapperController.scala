package controllers

import com.github.devcdcc.services.queue.Publisher
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
    extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def scrapUser(userId: Long) = Action.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(userId.toString))
  }

  def scrapMedia(userId: Long) = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

  def scrapFollowing(userId: Long) = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

  def scrapFollowers(userId: Long) = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

}
