package com.github.devcdcc.crawler.consumer.converters.request
import com.github.devcdcc.crawler.wrapper
import io.circe.Json

class MediaRequestConverter extends AbstractRequestConverter[wrapper.MediaRequest] {

  def elementType: Option[String] = Some("media")

  def convert: (wrapper.MediaRequest, Json) => wrapper.MediaRequest =
    (request, json) => {
      request
    }
}
