package com.github.devcdcc.crawler.consumer.converters.media

import com.github.devcdcc.crawler.consumer.converters.AbstractConverter
import io.circe.Json
import services.random.RandomGenerator

trait AbstractMediaConverter extends AbstractConverter[Int, Json, Json => Json] {

  def elementType: Int

  def isRequiredType(json: Json): Boolean =
    json.hcursor.downField("media_type").as[Int].fold(_ => false, c => c == elementType)
  def convert: Json => Json
}
