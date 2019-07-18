package com.github.devcdcc.crawler

import io.circe.Json

package object wrapper {

  trait QueueRequest {
    def userId: String
    def next_max_id: Option[String]
    def hasNext: Option[Boolean]
    def requestId: Option[String]
    def scrapperId: Option[String]
    def filter: Option[io.circe.Json]

  }

  case class UserRequest(
      userId: String,
      recursive: Option[Boolean] = None,
      next_max_id: Option[String] = None,
      hasNext: Option[Boolean] = None,
      requestId: Option[String] = None,
      scrapperId: Option[String] = None,
      filter: Option[Json] = None)
      extends QueueRequest

  implicit class PathHelper[T <: QueueRequest](request: T) {
    private val nodeURL = "localhost:3000/"
    private def userId  = request.userId

    final def userPath: String          = nodeURL + s"/user/$userId"
    final def userMediaPath: String     = nodeURL + s"/user/$userId/media"
    final def userResolvePath: String   = nodeURL + s"/user/$userId/resolve"
    final def userFollowingPath: String = nodeURL + s"/user/$userId/following"
    final def userFollowersPath: String = nodeURL + s"/user/$userId/followers"
  }
}
