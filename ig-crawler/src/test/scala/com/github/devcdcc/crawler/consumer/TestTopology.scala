package com.github.devcdcc.crawler.consumer

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.{Topology, TopologyTestDriver}

class TestTopology(messages: Iterable[ConsumerRecord[Array[Byte], Array[Byte]]]) extends TopologyTrait {

  private var topology: Topology = _
  private var props: Properties  = _

  override def set(topology: Topology, props: Properties): Unit = {
    this.topology = topology
    this.props = props
  }

  lazy val stream = new TopologyTestDriver(topology, props)
  override def start: Unit =
    messages.foreach(record => {
      stream.pipeInput(record)
    })
  override def close: Unit = stream.close()

}
