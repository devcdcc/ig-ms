package com.github.devcdcc.crawler.consumer

import java.util.Properties

import org.apache.kafka.streams.{KafkaStreams, Topology}

class KafkaStreamTopology(topology: Topology, props: Properties)
    extends TopologyTrait(topology = topology, props = props) {

  private val stream       = new KafkaStreams(topology, props)
  override def start: Unit = stream.start()
  override def close: Unit = stream.close()

}
