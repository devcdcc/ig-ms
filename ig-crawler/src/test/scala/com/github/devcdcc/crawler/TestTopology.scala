package com.github.devcdcc.crawler

import java.util.Properties

import com.github.devcdcc.crawler.consumer.TopologyTrait
import org.apache.kafka.streams.{Topology, TopologyTestDriver}

class TestTopology(topology: Topology, props: Properties) extends TopologyTrait(topology = topology, props = props) {

  private val stream       = new TopologyTestDriver(topology, props)
  override def start: Unit = ()
  override def close: Unit = stream.close()

}
