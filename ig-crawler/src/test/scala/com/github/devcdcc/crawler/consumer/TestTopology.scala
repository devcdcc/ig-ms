package com.github.devcdcc.crawler.consumer

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.{Topology, TopologyTestDriver}

class TestTopology extends TopologyTrait {

  private var topology: Topology                                   = _
  private var props: Properties                                    = _
  var messages: Iterable[ConsumerRecord[Array[Byte], Array[Byte]]] = Iterable.empty

  def setMessages(messages: Iterable[ConsumerRecord[Array[Byte], Array[Byte]]]) =
    this.messages = messages

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
