package com.github.devcdcc.crawler.consumer

import org.apache.kafka.streams.TopologyTestDriver

class OrchestrationTestTopology extends OrchestrationTrait[TopologyTestDriver] {
  lazy val kafkaStreams = new TopologyTestDriver(build, props)
  def start(): Unit     = ()
  def close(): Unit     = kafkaStreams.close()
}
