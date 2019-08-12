package com.github.devcdcc.media

import io.circe.Json
import services.random.RandomGenerator

trait MediaConverter {

  protected def mediaType: Int
  def convert: Json => Json

  def isMediaType(json: Json): Boolean =
    json.hcursor.downField("media_type").as[Int].fold(_ => false, c => c == mediaType)
}
