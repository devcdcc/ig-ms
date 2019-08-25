package com.github.devcdcc.crawler.consumer.converters.request

import com.github.devcdcc.crawler.consumer.converters.AbstractConverter
import com.github.devcdcc.crawler.wrapper.QueueRequest
import io.circe.Json

trait AbstractRequestConverter[A <: QueueRequest] extends AbstractConverter[Option[String], A, (A, Json) => A] {
  override def isRequiredType(input: A): Boolean = input.requestType == elementType
}
