package com.github.devcdcc.crawler

import com.github.devcdcc.crawler.consumer.Orchestration
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.scalatest.WordSpec

class OrchestrationSpec extends WordSpec {
  val factory = new ConsumerRecordFactory("topic1", new StringSerializer, new StringSerializer)

  val message1: ConsumerRecord[Array[Byte], Array[Byte]] = factory.create("")
  val messages                                           = List(message1)
  val subject                                            = new Orchestration(new TestTopology(messages))
  it.toString when {
    "" should {
      "" in {}
    }
  }
}
