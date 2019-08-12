package com.github.devcdcc.crawler.consumer.builder

import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import com.github.devcdcc.media.{CarouselMediaConverter, MediaConverter}
import io.circe._
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.{StreamsBuilder, _}
import org.apache.kafka.streams.scala.kstream._

class MediaScrapperBuilder(builder: StreamsBuilder, converters: List[MediaConverter])
    extends BasicJsonStringBuilder(builder = builder, topic = TopicsHelper.userMediaScrapperTopic)
    with MediaJsonFlatter {

  private def mediaElements =
    parseSuccess(jsonStream)
      .flatMapValues(value => flatter(value))
//      .mapValues(json => json.noSpaces)
  //  .to(s"${TopicsHelper.mediaElementScrapperTopic}")

  private def mapMediaElement: Json => Either[String, Json] = { json =>
    converters.find(converter => converter.isMediaType(json)) match {
      case None            => Left(json.noSpaces)
      case Some(converter) => Right(converter.convert(json))
    }
  }

  private def convertMediaElements =
    mediaElements.mapValues(json => mapMediaElement(json))

  override def transact: Unit = {
    super.transact
    val tempMediaElements = this.convertMediaElements
    parseFails(tempMediaElements, TopicsHelper.mediaElementScrapperTopic)
    parseSuccess(tempMediaElements).mapValues(_.noSpaces).to(TopicsHelper.mediaElementScrapperTopic)
  }
}
