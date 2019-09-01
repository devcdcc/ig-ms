package com.github.devcdcc
import io.circe.Json
package object domain {

  trait QueueRequest {
    def userId: String
    def requestType: Option[String]
    def next_max_id: Option[String]
    def hasNext: Option[Boolean]
    def requestId: Option[String]
    def scrapperId: Option[String]
    def filter: Option[Json]

  }

  case class MediaRequest(
      userId: String,
      next_max_id: Option[String] = None,
      hasNext: Option[Boolean] = None,
      requestId: Option[String] = None,
      scrapperId: Option[String] = None,
      filter: Option[Json] = None)
      extends QueueRequest {
    def requestType: Option[String] = Some("MediaRequest")
  }

  case class UserRequest(
      userId: String,
      next_max_id: Option[String] = None,
      hasNext: Option[Boolean] = None,
      requestId: Option[String] = None,
      scrapperId: Option[String] = None,
      filter: Option[Json] = None,
      recursive: Option[Boolean] = None)
      extends QueueRequest {
    def requestType: Option[String] = Some("UserRequest")
  }

  case class RequestUrl(url: Option[String] = None)

  private val DEFAULT_REQUEST_URL    = RequestUrl()
  final private val DEFAULT_NODE_URL = "localhost:3000/"

  implicit class PathHelper[T <: QueueRequest](request: T) {

    private def nodeURL(implicit requestUrl: RequestUrl) =
      requestUrl.url.getOrElse(
        request.scrapperId
          .map(scrapperId => s"$scrapperId.$DEFAULT_NODE_URL")
          .getOrElse(DEFAULT_NODE_URL)
      )

    def requestURl(implicit requestUrl: RequestUrl = DEFAULT_REQUEST_URL): String =
      s"$nodeURL/user/${request.userId}/${request.requestType.getOrElse("")}"

  }

}
