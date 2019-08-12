package com.github.devcdcc.crawler.consumer

import java.util.Properties

import org.apache.kafka.streams.{KafkaStreams, Topology}

class KafkaStreamTopology extends TopologyTrait {

  private var topology: Topology = _
  private var props: Properties  = _

  override def set(topology: Topology, props: Properties): Unit = {
    this.topology = topology
    this.props = props
  }

  private val stream       = new KafkaStreams(topology, props)
  override def start: Unit = stream.start()
  override def close: Unit = stream.close()

}
