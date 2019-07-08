package controllers

import javax.inject._
import play.api.mvc._

import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class UserController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

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

  def user(userId: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    ???
  }

  def userMedia(userId: Long, next_max_id: Option[String]): Action[AnyContent] = Action.async { request =>
    Future.successful(Ok(request.path))
  }

  def userResolve(userId: Long, next_max_id: Option[String]): Action[AnyContent] = ???

  def userFollowing(userId: Long, next_max_id: Option[String]): Action[AnyContent] = ???

  def userFollowers(userId: Long, next_max_id: Option[String]): Action[AnyContent] = ???

}
