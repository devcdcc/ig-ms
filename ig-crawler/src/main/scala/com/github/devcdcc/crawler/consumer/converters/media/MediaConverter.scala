package com.github.devcdcc.crawler.consumer.converters.media

import com.github.devcdcc.crawler.consumer.converters.AbstractConverter
import io.circe.Json
import services.random.RandomGenerator

trait MediaConverter extends AbstractConverter[Int, Json, Json] {

  def elementType: Int
  def convert: Json => Json

  def isRequiredType(json: Json): Boolean =
    json.hcursor.downField("media_type").as[Int].fold(_ => false, c => c == elementType)
}
