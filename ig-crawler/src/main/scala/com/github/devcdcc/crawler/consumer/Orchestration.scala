package com.github.devcdcc.crawler.consumer

import java.util.Properties

import com.github.devcdcc.crawler.consumer.builder.AbstractBuilder
import com.github.devcdcc.crawler.consumer.builder.processor.MediaScrapperBuilder
import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper._
import com.github.devcdcc.crawler.consumer.converters.media.{
  AbstractMediaConverter,
  CarouselMediaConverter,
  SimpleMediaConverter
}
import org.apache.kafka.streams.{StreamsConfig, Topology}
import org.apache.kafka.streams.scala._

class Orchestration[T <: TopologyTrait](topology: T) {
  val converters: List[AbstractMediaConverter] = List(new SimpleMediaConverter, new CarouselMediaConverter)

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getString("app.id"))
    p.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("app.kafka-clients.bootstrap.servers"))
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    p
  }

  val builder: StreamsBuilder                          = new StreamsBuilder
  val scrappers: List[AbstractBuilder[String, String]] = List(new MediaScrapperBuilder(builder, converters))
  lazy val build: Topology                             = builder.build()

  def start(): Unit = {
    scrappers.foreach(_.transact)
    topology.set(build, props)
    topology.start
//    sys.ShutdownHookThread {
//      topology.close
//    }
  }
}
