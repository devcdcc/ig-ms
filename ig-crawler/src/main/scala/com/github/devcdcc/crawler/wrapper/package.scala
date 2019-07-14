package com.github.devcdcc.crawler

package object wrapper {

  trait QueueRequest {
    def userId: String
    def next_max_id: Option[String]
    def hasNext: Option[Boolean]
    def id: Option[String]

  }

  case class UserRequest(
      userId: String,
      recursive: Option[Boolean] = None,
      next_max_id: Option[String] = None,
      hasNext: Option[Boolean] = None,
      id: Option[String] = None)
      extends QueueRequest

  implicit class PathHelper(request: QueueRequest) {
    private val nodeURL = "localhost:3000/"
    private def userId  = request.userId

    final def userPath: String          = nodeURL + s"/user/$userId"
    final def userMediaPath: String     = nodeURL + s"/user/$userId/media"
    final def userResolvePath: String   = nodeURL + s"/user/$userId/resolve"
    final def userFollowingPath: String = nodeURL + s"/user/$userId/following"
    final def userFollowersPath: String = nodeURL + s"/user/$userId/followers"
  }

}
