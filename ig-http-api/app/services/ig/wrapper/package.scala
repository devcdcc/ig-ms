package services.ig

import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.Future

package object wrapper {

  import paths._

  trait IGRequest {
    def patternMatch: String
    def matchRequest(implicit request: Request[AnyContent]): Boolean = request.path.matches(patternMatch)
    def doCall(implicit ws: WSClient): WSRequest
  }
  case class User(userId: String) extends IGRequest {

    override def patternMatch: String = userPath

    override def doCall(implicit ws: WSClient): WSRequest = ws.url(nodeURL + userId)
  }

  private object paths {
    val nodeURL           = "localhost:3000/"
    val userPath          = "/user/\\d+"
    val userMediaPath     = "/user/\\d+/media"
    val userResolvePath   = "/user/\\d+/resolve"
    val userFollowingPath = "/user/\\d+/following"
    val userFollowersPath = "/user/\\d+/followers"
  }

}
