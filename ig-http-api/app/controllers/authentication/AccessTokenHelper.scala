package controllers.authentication

import play.api.libs.json.Json
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.Unauthorized

import scala.concurrent.Future

trait AccessTokenHelper {
  val headerName = "X-Consumer-Username"

  private def unauthorizedResponse(option: Option[String]) = Unauthorized(Json.parse("{}"))

  private def _fromRequest[T](request: Request[T], suffix: String) =
    request.headers
      .get(headerName)
      .map(
        userName =>
          if (userName.endsWith(suffix)) Right(userName.dropRight(suffix.length())) else Left(Option(userName))
      )
      .getOrElse(Left(None))

  private def fromRequest[T](request: Request[T], suffix: String)(response: String => Result) =
    _fromRequest(request, suffix)
      .fold(fail => unauthorizedResponse(fail), c => response(c))
  private def fromAsyncRequest[T](request: Request[T], suffix: String)(response: String => Future[Result]) =
    _fromRequest(request, suffix)
      .fold(fail => Future.successful(unauthorizedResponse(fail)), c => response(c))

  def authenticatedPrivateSiteIdAsync[T](response: String => Future[Result])(implicit request: Request[T]) =
    fromAsyncRequest(request, "_private")(response)

  def authenticatedPrivateSiteId[T](response: String => Result)(implicit request: Request[T]) =
    fromRequest(request, "_private")(response)

  def authenticatedPublicSiteIdAsync[T](response: String => Future[Result])(implicit request: Request[T]) =
    fromAsyncRequest(request, "_public")(response)

  def authenticatedPublicSiteId[T](response: String => Result)(implicit request: Request[T]) =
    fromRequest(request, "_public")(response)

  def authenticatedTokenSiteIdAsync[T](response: String => Future[Result])(implicit request: Request[T]) =
    fromAsyncRequest(request, "_token")(response)

  def authenticatedTokenSiteId[T](response: String => Result)(implicit request: Request[T]) =
    fromRequest(request, "_token")(response)

//  def authenticatedPrivateSiteId[T](implicit request: Request[T]) =
//    fromRequest(request, "_private")
//  def authenticatedPublicSiteId[T](implicit request: Request[T]) =
//    fromRequest(request, "_public")
//
//  def authenticatedTokenSiteId[T](implicit request: Request[T]) =
//    fromRequest(request, "_token")

}
