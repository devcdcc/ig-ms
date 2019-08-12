package com.github.devcdcc.crawler.consumer

import java.util.Properties

import org.apache.kafka.streams.Topology

abstract class TopologyTrait(topology: Topology, props: Properties) {
  def start: Unit
  def close: Unit
}
