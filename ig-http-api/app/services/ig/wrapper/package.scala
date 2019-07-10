package services.ig

import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.Future

package object wrapper {

  import paths._

  trait IGRequest {
    def next_max_id: Option[String]
    def hasNext: Option[Boolean]
    def id: Option[String]
  }
  case class User(
      userId: String,
      next_max_id: Option[String] = None,
      hasNext: Option[Boolean] = None,
      id: Option[String] = None)
      extends IGRequest

  private object paths {
    val nodeURL           = "localhost:3000/"
    val userPath          = "/user/\\d+"
    val userMediaPath     = "/user/\\d+/media"
    val userResolvePath   = "/user/\\d+/resolve"
    val userFollowingPath = "/user/\\d+/following"
    val userFollowersPath = "/user/\\d+/followers"
  }

}
