package controllers.authentication

import play.api.libs.json.Json
import play.api.mvc.Request
import play.api.mvc.Results.Unauthorized

trait AccessTokenHelper {
  val headerName = "X-Consumer-Username"

  private def unauthorizedResponse(option: Option[String]) = Unauthorized(Json.parse("{}"))

  private def fromRequest[T](request: Request[T], suffix: String) =
    request.headers
      .get(headerName)
      .map(
        userName =>
          if (userName.endsWith(suffix)) Right(userName.dropRight(suffix.length())) else Left(Option(userName))
      )
      .getOrElse(Left(None))
      .fold(fail => Left(unauthorizedResponse(fail)), c => Right(c))

  def authenticatedPrivateSiteId[T](implicit request: Request[T]) = fromRequest(request, "_private")
  def authenticatedPublicSiteId[T](implicit request: Request[T])  = fromRequest(request, "_public")
  def authenticatedTokenSiteId[T](implicit request: Request[T])   = fromRequest(request, "_token")

}
