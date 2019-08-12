package com.github.devcdcc

import com.github.devcdcc.crawler.consumer.{KafkaStreamTopology, Orchestration}

object Main extends App {
  private val orchestration = new Orchestration[KafkaStreamTopology]
  orchestration.start
}
