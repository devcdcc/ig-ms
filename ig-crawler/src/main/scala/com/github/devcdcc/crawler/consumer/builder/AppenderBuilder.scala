package com.github.devcdcc.crawler.consumer.builder

import com.github.devcdcc.crawler.consumer.converters.request.AbstractRequestConverter
import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import com.github.devcdcc.domain
import io.circe.generic.auto._
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.parser.decode
import io.circe.syntax._

class AppenderBuilder[T <: domain.QueueRequest](
    builder: StreamsBuilder,
    override val topic: String = TopicsHelper.appenderTopic,
    stream: Option[KStream[String, String]] = None,
    converters: List[AbstractRequestConverter[T]])
    extends AbstractBuilder[String, String] {

  val topicStream: KStream[String, String] = stream.getOrElse(builder.stream(topic))

//  private val parseStream = topicStream.mapValues((key, value) => )

  override def transact: Unit = {
    super.transact
    val x = ""
  }
}
