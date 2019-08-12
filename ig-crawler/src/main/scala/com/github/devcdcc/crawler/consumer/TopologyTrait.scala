package com.github.devcdcc.crawler.consumer

import java.util.Properties

import org.apache.kafka.streams.Topology

abstract class TopologyTrait {
  def set(topology: Topology, props: Properties): Unit
  def start: Unit
  def close: Unit
}
