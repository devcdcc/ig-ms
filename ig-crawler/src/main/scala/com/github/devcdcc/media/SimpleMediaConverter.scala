package com.github.devcdcc.media
import io.circe.Json
import cats.syntax.either._
import io.circe._
import io.circe.parser._
import io.circe.optics.JsonPath._
import services.random.RandomGeneratorObject

class SimpleMediaConverter extends MediaConverter {

  protected def mediaType: Int = 1
  override def convert: Json => Json =
    root.image_versions2.candidates.url.string.modify(_ => RandomGeneratorObject.generator.generate())
}
