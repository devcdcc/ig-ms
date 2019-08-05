package com.github.devcdcc.crawler.consumer.builder

import io.circe.Json
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.Serdes._

abstract class BasicJsonStringBuilder(builder: StreamsBuilder, override val topic: String)
    extends AbstractBuilder[String, String] {

  def parseJson(json: String): Either[String, Json] =
    io.circe.parser.parse(json).fold(fail => Left(json), parsed => Right(parsed))

  override val topicStream: KStream[String, String] = builder.stream(topic)
  private val jsonStream                            = topicStream.mapValues(value => parseJson(value))

  protected def parseSuccess =
    jsonStream
      .filter((key, value) => value.isRight)
      .mapValues(
        either => either match { case Right(value) => value }
      )

  protected def parseFails =
    jsonStream
      .filter((key, value) => value.isLeft)
      .mapValues(
        either => either match { case Left(value) => value }
      )
      .to(s"$topic.fail")

  /**
    * this method must be called from child [[transact]]
    */
  override def transact: Unit = parseFails
}
