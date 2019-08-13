package com.github.devcdcc.crawler

import com.github.devcdcc.crawler.consumer.{Orchestration, TestTopology}
import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.scalatest.{MustMatchers, WordSpec}
import io.circe.parser._
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

class OrchestrationSpec extends WordSpec with MustMatchers with TestMessages with MockitoSugar {

  val factory =
    new ConsumerRecordFactory(TopicsHelper.userMediaScrapperTopic, new StringSerializer, new StringSerializer)

  val message1: ConsumerRecord[Array[Byte], Array[Byte]] = factory.create(onlyMedia1)
  val messages                                           = List(message1)
  val topology                                           = new TestTopology(messages)
  val subject                                            = new Orchestration(topology = topology)
  "valid stream" when {
    "messages are non empty" should {
      "get valid json" in {

        //given

        //when
        subject.start

        //then
        parse(
          topology.stream
            .readOutput(TopicsHelper.mediaElementScrapperTopic, new StringDeserializer, new StringDeserializer)
            .value()
        ).isRight mustBe true
      }
    }
  }
}
