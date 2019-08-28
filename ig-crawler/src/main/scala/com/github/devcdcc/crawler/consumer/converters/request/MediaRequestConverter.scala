package com.github.devcdcc.crawler.consumer.converters.request
import com.github.devcdcc.crawler.wrapper
import wrapper.PathHelper
import io.circe.Json
import org.http4s.{MediaType, Method, Query, Uri}
import org.http4s.circe.CirceEntityDecoder._

class MediaRequestConverter extends AbstractRequestConverter[wrapper.MediaRequest, Either[Throwable, Json]] {

  val elementType: Option[String] = Some("media")

  def convert: (wrapper.MediaRequest, Json) => wrapper.MediaRequest =
    (request, json) =>
      request.copy(
        scrapperId = json.hcursor.downField("scrapperId").as[String].toOption,
        next_max_id = json.hcursor.downField("next_max_id").as[String].toOption,
        hasNext = json.hcursor.downField("more_available").as[Boolean].toOption
      )

  override def doRequest(a: wrapper.MediaRequest): Either[Throwable, Json] = {
    val uri = Uri(path = a.requestURl, query = Query.empty :+ ("next_max_id", a.next_max_id))
    httpClient.expect[Json](uri).attempt.unsafeRunSync()
  }
}
