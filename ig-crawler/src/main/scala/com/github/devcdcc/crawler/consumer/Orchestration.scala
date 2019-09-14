package com.github.devcdcc.crawler.consumer

import org.apache.kafka.streams.KafkaStreams

class Orchestration extends OrchestrationTrait[KafkaStreams] {
  lazy val kafkaStreams = new KafkaStreams(build, props)
  def start(): Unit     = kafkaStreams.start()
  def close(): Unit     = kafkaStreams.close()
}
