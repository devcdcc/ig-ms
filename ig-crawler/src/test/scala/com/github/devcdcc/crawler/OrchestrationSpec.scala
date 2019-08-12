package com.github.devcdcc.crawler

import com.github.devcdcc.crawler.consumer.{Orchestration, TestTopology}
import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.scalatest.WordSpec

class OrchestrationSpec extends WordSpec with TestMessages {

  val factory =
    new ConsumerRecordFactory(TopicsHelper.userMediaScrapperTopic, new StringSerializer, new StringSerializer)

  val message1: ConsumerRecord[Array[Byte], Array[Byte]] = factory.create(onlyMedia8)
  val messages                                           = List(message1)
  val topology                                           = new TestTopology(messages)
  val subject                                            = new Orchestration(topology = topology)
  "works" when {
    "topic is non null" should {
      "don't throws exception" in {
        subject.start
        Thread.sleep(1000)
        println(
          topology.stream
            .readOutput(TopicsHelper.mediaElementScrapperTopic, new StringDeserializer, new StringDeserializer)
            .value()
        )
      }
    }
  }
}
