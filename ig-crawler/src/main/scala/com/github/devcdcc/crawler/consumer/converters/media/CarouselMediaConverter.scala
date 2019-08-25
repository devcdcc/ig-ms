package com.github.devcdcc.crawler.consumer.converters.media

import io.circe.Json
import io.circe.optics.JsonPath._
import services.random.RandomGeneratorObject

class CarouselMediaConverter extends MediaConverter {

  def elementType: Int = 8

  override def convert: Json => Json =
    root.carousel_media.each.image_versions2.candidates.each.json.modify(
      json =>
        json.deepMerge(
          Json
            .obj(("hash_image_reference", Json.fromString(RandomGeneratorObject.generator.generate())))
        )
    )
}
