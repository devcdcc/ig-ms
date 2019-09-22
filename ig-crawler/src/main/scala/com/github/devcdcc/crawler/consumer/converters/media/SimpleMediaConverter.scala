package com.github.devcdcc.crawler.consumer.converters.media
import io.circe.Json
import io.circe.optics.JsonPath._
import services.random.RandomGeneratorObject

class SimpleMediaConverter extends AbstractMediaConverter {

  def elementType: Int = 1
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
