package com.github.devcdcc.crawler.consumer.builder

import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import io.circe._
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.{StreamsBuilder, _}
import org.apache.kafka.streams.scala.kstream._

class MediaScrapperBuilder(builder: StreamsBuilder)
    extends BasicJsonStringBuilder(builder = builder, topic = TopicsHelper.userMediaScrapperTopic)
    with MediaJsonFlatter {

  private def processMediaElements =
    parseSuccess
      .flatMapValues(value => flatter(value))
      .mapValues(json => json.noSpaces)
      .to(s"${TopicsHelper.mediaElementScrapperTopic}")

  override def transact: Unit = {
    super.transact
    this.processMediaElements
  }
}
