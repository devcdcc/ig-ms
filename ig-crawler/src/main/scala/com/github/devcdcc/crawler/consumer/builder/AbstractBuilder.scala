package com.github.devcdcc.crawler.consumer.builder

import io.circe.Json
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

trait AbstractBuilder[K, V] {
  val topic: String
  val topicStream: KStream[K, V]
  def transact: Unit = ()
}
