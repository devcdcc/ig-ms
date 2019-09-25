package com.github.devcdcc.crawler.consumer.converters.request

import com.github.devcdcc.domain
import io.circe.Json

class MediaRequestConverter extends AbstractRequestConverter[domain.MediaRequest] {

  val elementType: Option[String] = Some("media")

  def convert: (domain.MediaRequest, Json) => Either[Throwable, domain.MediaRequest] =
    (originalRequest, json) => {
      val moreAvailable = json.hcursor.downField("more_available").as[Boolean].toOption
      if (moreAvailable.isDefined) {
        Right(
          originalRequest.copy(
            scrapperId = json.hcursor.downField("scrapperId").as[String].toOption,
            next_max_id = json.hcursor.downField("next_max_id").as[String].toOption,
            hasNext = moreAvailable
          )
        )
      } else Left(new NoSuchElementException("field more_available is false"))
    }
}
