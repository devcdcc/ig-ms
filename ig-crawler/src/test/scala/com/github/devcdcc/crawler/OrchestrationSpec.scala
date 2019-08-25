package com.github.devcdcc.crawler

import com.github.devcdcc.crawler.consumer.{Orchestration, TestTopology}
import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.scalatest.{MustMatchers, WordSpec}
import io.circe.parser._
import io.circe.optics.JsonPath._
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

class OrchestrationSpec extends WordSpec with MustMatchers with TestMessages with MockitoSugar {

  val factory =
    new ConsumerRecordFactory(TopicsHelper.userMediaScrapperTopic, new StringSerializer, new StringSerializer)
  "valid stream" when {
    "message as valid json" should {
      "return okay for valid media json" in {

        //given
        val topology                                           = new TestTopology
        val subject                                            = new Orchestration(topology = topology)
        val message1: ConsumerRecord[Array[Byte], Array[Byte]] = factory.create(onlyMedia1)
        val messages                                           = List(message1)

        //when
        topology.setMessages(messages)
        subject.start()

        //then
        val result = topology.stream
          .readOutput(TopicsHelper.mediaElementScrapperTopic, new StringDeserializer, new StringDeserializer)
          .value()

        parse(result).fold(
          fail => throw new Exception("Unexpected Exception"),
          json => {
            assert(root.id.string.nonEmpty(json))
            assert(root.pk.string.nonEmpty(json))
            assert(root.media_type.int.getOption(json).contains(1))
            assert(root.image_versions2.candidates.each.url.string.getAll(json).nonEmpty)
            assert(root.image_versions2.candidates.each.hash_image_reference.string.getAll(json).nonEmpty)
          }
        )
        topology.close
      }
      s"don't insert a element in ${TopicsHelper.mediaElementScrapperTopic} when is invalid media Json" in {

        //given
        val topology                                           = new TestTopology
        val subject                                            = new Orchestration(topology = topology)
        val message1: ConsumerRecord[Array[Byte], Array[Byte]] = factory.create("{}")
        val messages                                           = List(message1)

        //when
        topology.setMessages(messages)
        subject.start()

        //then
        val result = topology.stream
          .readOutput(TopicsHelper.mediaElementScrapperTopic, new StringDeserializer, new StringDeserializer)

        assertThrows[NullPointerException] {
          parse(result.value()).fold(_ => (), fail())
        }
        topology.close
      }
    }
  }
}
