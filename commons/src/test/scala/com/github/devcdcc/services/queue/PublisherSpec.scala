package com.github.devcdcc.services.queue
import com.github.devcdcc.services.queue.publishers.kafka.KafkaPublisher

class PublisherSpec {
  val publisher: Publisher = new KafkaPublisher
}
