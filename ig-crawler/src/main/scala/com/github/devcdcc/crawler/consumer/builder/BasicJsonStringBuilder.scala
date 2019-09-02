package com.github.devcdcc.crawler.consumer.builder

import com.github.devcdcc.helpers.TopicsHelper._
import io.circe.Json
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.Serdes._

abstract class BasicJsonStringBuilder(
    builder: StreamsBuilder,
    override val topic: String,
    stream: Option[KStream[String, String]] = None)
    extends AbstractBuilder[String, String] {

  def parseJson(json: String): Either[String, Json] =
    io.circe.parser.parse(json).fold(fail => Left(json), parsed => Right(parsed))

  val topicStream: KStream[String, String]                        = stream.getOrElse(builder.stream(topic))
  protected val jsonStream: KStream[String, Either[String, Json]] = topicStream.mapValues(value => parseJson(value))

  protected def parseSuccess(stream: KStream[String, Either[String, Json]]): KStream[String, Json] =
    stream
      .filter((key, value) => value.isRight)
      .mapValues(either => either.toOption.head)

  protected def parseFails(stream: KStream[String, Either[String, Json]], topic: String = this.topic): Unit =
    stream
      .filter((key, value) => value.isLeft)
      .mapValues(either => either.swap.toOption.head)
      .to(s"$topic.$parseErrorTopicLabel")

  /**
    * this method must be called from child [[transact]]
    */
  override def transact: Unit = parseFails(jsonStream)
}
