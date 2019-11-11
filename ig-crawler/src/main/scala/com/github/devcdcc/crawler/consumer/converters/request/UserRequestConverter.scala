package com.github.devcdcc.crawler.consumer.converters.request

import com.github.devcdcc.domain
import com.github.devcdcc.domain.QueueRequest
import io.circe.Json

class UserRequestConverter extends AbstractRequestConverter {

  override def isRequiredType(input: QueueRequest): Boolean = input.isInstanceOf[domain.MediaRequest]

  def convert: (domain.QueueRequest, Json) => Either[Throwable, domain.QueueRequest] =
    (originalRequest, json) => {
      val moreAvailable = json.hcursor.downField("more_available").as[Boolean].toOption
      if (moreAvailable.isDefined) {
        Right(
          originalRequest
            .asInstanceOf[domain.UserRequest]
            .copy(
              scrapperId = json.hcursor.downField("scrapperId").as[String].toOption,
              next_max_id = json.hcursor.downField("next_max_id").as[String].toOption,
              hasNext = moreAvailable
            )
        )
      } else Left(new NoSuchElementException("field more_available is false"))
    }
}
