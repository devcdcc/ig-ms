package com.github.devcdcc.crawler

import com.github.devcdcc.crawler.consumer.Orchestration
import org.apache.kafka.streams.TopologyTestDriver

class OrchestrationSpec {
  val subject = new Orchestration[TestTopology]
}
