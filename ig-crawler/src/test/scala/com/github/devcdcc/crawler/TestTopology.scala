package com.github.devcdcc.crawler

import java.util.Properties

import com.github.devcdcc.crawler.consumer.TopologyTrait
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.apache.kafka.streams.{Topology, TopologyTestDriver}

import scala.collection.JavaConverters._

class TestTopology(messages: Iterable[ConsumerRecord[Array[Byte], Array[Byte]]]) extends TopologyTrait {

  private var topology: Topology = _
  private var props: Properties  = _

  override def set(topology: Topology, props: Properties): Unit = {
    this.topology = topology
    this.props = props
  }

  private lazy val stream  = new TopologyTestDriver(topology, props)
  override def start: Unit = messages.foreach(record => stream.pipeInput(record))
  override def close: Unit = stream.close()

}
