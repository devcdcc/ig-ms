package com.github.devcdcc.crawler.consumer.converters.request
import com.github.devcdcc.domain
import io.circe.Json

class MediaRequestConverter extends AbstractRequestConverter[domain.MediaRequest] {

  val elementType: Option[String] = Some("media")

  def convert: (domain.MediaRequest, Json) => domain.MediaRequest =
    (request, json) => {
      request.copy(
        scrapperId = json.hcursor.downField("scrapperId").as[String].toOption,
        next_max_id = json.hcursor.downField("next_max_id").as[String].toOption,
        hasNext = json.hcursor.downField("more_available").as[Boolean].toOption
      )
    }
}
