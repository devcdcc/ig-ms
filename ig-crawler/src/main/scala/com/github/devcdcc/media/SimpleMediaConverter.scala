package com.github.devcdcc.media
import io.circe.Json
import cats.syntax.either._
import io.circe._
import io.circe.parser._
import io.circe.optics.JsonPath._
import services.random.RandomGeneratorObject
import io.circe.generic.auto._
import io.circe.syntax._

class SimpleMediaConverter extends MediaConverter {

  protected def mediaType: Int = 1
  override def convert: Json => Json =
    root.image_versions2.candidates.each.json
      .modify(
        json =>
          json.deepMerge(
            Json
              .obj(("hash_image_reference", Json.fromString(RandomGeneratorObject.generator.generate())))
          )
      )
}
