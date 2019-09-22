package com.github.devcdcc.crawler.consumer.builder

import com.github.devcdcc.crawler.api.exception.NextElementNotFoundException
import com.github.devcdcc.crawler.consumer.converters.request.AbstractRequestConverter
import com.github.devcdcc.domain
import com.github.devcdcc.helpers.TopicsHelper
import io.circe.Json
import io.circe.generic.auto._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

class AppenderBuilder[T <: domain.QueueRequest](
    builder: StreamsBuilder,
    override val topic: String = TopicsHelper.appenderTopic,
    stream: Option[KStream[String, String]] = None,
    converters: List[AbstractRequestConverter[T]])
    extends BasicJsonStringBuilder(builder = builder, topic = topic, stream = stream) {

  private[builder] def doRequest(original: T): scala.Either[scala.Throwable, _root_.io.circe.Json] =
    converters.find(_.isRequiredType(original)) match {
      case None            => Left(new NoSuchElementException("AbstractRequestConverter can't be found"))
      case Some(converter) => converter.doRequest(original)
    }

  private[builder] def getNextRequest(original: T, response: Json): Either[Throwable, T] =
    converters.find(_.isRequiredType(original)) match {
      case None            => Left(NextElementNotFoundException("AbstractRequestConverter can't be found"))
      case Some(converter) => converter.convert(original, response)
    }

  private[builder] def jsonToRequest: Json => Either[Json, domain.QueueRequest] = { json =>
    json.as[domain.QueueRequest].fold(_ => Left(json), request => Right(request))
  }

  //  private val parseStream = topicStream.mapValues((key, value) => )
  private lazy val requestStream = parseSuccess(jsonStream).mapValues(value => value)
  override def transact: Unit =
    super.transact
}
