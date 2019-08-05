package com.github.devcdcc.media

import io.circe.Json
import io.circe.optics.JsonPath._
import services.random.RandomGeneratorObject

class CarouselMediaConverter extends MediaConverter {

  protected def mediaType: Int = 8

  override def convert: Json => Json =
    root.carousel_media.image_versions2.candidates.url.string.modify(_ => RandomGeneratorObject.generator.generate())
}
