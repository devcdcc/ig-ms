package com.github.devcdcc.crawler.consumer.builder

import com.github.devcdcc.crawler.api.exception.NextElementNotFoundException
import com.github.devcdcc.crawler.consumer.converters.request.AbstractRequestConverter
import com.github.devcdcc.domain
import com.github.devcdcc.domain.QueueRequest
import com.github.devcdcc.helpers.TopicsHelper
import io.circe.Json
import io.circe.syntax._
import io.circe.generic.auto._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

class AppenderBuilder[T <: domain.QueueRequest](
    builder: StreamsBuilder,
    override val topic: String = TopicsHelper.appenderTopic,
    stream: Option[KStream[String, String]] = None,
    converters: List[AbstractRequestConverter])
    extends BasicJsonStringBuilder(builder = builder, topic = topic, stream = stream) {

  private[builder] def doRequest(original: QueueRequest): scala.Either[scala.Throwable, io.circe.Json] =
    converters.find(_.isRequiredType(original)) match {
      case None            => Left(new NoSuchElementException("AbstractRequestConverter can't be found"))
      case Some(converter) => converter.doRequest(original)
    }

  sealed case class NextBuild(original: QueueRequest, response: Either[Throwable, QueueRequest]);
  private[builder] def getNextRequest(original: QueueRequest, response: Json): NextBuild = NextBuild(
    original,
    if (original.hasNext.contains(false))
      Left(NextElementNotFoundException())
    else
      converters.find(_.isRequiredType(original)) match {
        case None            => Left(NextElementNotFoundException("AbstractRequestConverter can't be found"))
        case Some(converter) => converter.convert(original, response)
      }
  )

  private[builder] def jsonToRequest: Json => Either[Json, domain.QueueRequest] = { json =>
    json.as[domain.QueueRequest].fold(_ => Left(json), request => Right(request))
  }

  //  private val parseStream = topicStream.mapValues((key, value) => )
  private lazy val queueRequestStream = parseSuccess(jsonStream).mapValues(jsonToRequest)
  private lazy val failedQueueRequest =
    queueRequestStream.flatMapValues((key, value) => value.swap.toOption).mapValues(_.noSpaces)
  private lazy val httpResponseStream = validQueueRequest.mapValues(
    request => doRequest(request).fold(fail => Left(request, fail), value => Right(request, value))
  )
  def validQueueRequest = queueRequestStream.flatMapValues((key, value) => value.toOption)
  /*

   */
  override def transact: Unit = {
    super.transact
    failedQueueRequest.to(s"$topic.error.parsing")(null)
    httpResponseStream
      .flatMapValues((_, value) => value.swap.toOption)
      .mapValues((key, error) => error._1.asJson.noSpaces)
      .to(s"$topic.error.request")(null)
    val validResponse = httpResponseStream
      .flatMapValues((_, value) => value.toOption)
    validResponse
      .mapValues(_._2.noSpaces)
      .to(s"$topic.response")(null)
    val nextResponse = validResponse.mapValues((key, response) => validResponseToNextElement(response))
    nextResponse.flatMapValues(_.swap.toOption).mapValues(_._1.asJson.noSpaces).to(s"$topic.error.next")(null)
    nextResponse.flatMapValues(_.toOption).mapValues(_.asJson.noSpaces).to(topic)(null)
  }

  private def validResponseToNextElement(response: (QueueRequest, Json)) = {
    val (request, value) = response
    getNextRequest(request, value) match {
      case NextBuild(original, Left(failure)) =>
        Left(original, failure)
      case NextBuild(original, Right(nextBuild)) =>
        Right(nextBuild)
    }
  }
}
