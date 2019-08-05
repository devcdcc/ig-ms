package com.github.devcdcc.crawler.consumer

import java.time.Duration
import java.util.Properties

import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

class Orchestration extends TopicsHelper {
  import Serdes._

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getString("app.id"))
    p.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE)
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("app.kafka-clients.bootstrap.servers"))
    p
  }

  val builder: StreamsBuilder                   = new StreamsBuilder
  val users: KStream[String, String]            = builder.stream[String, String](userScrapperTopic)
  val usersMedia: KStream[String, String]       = builder.stream[String, String](userMediaScrapperTopic)
  val mediaElement: KStream[String, String]     = builder.stream[String, String](mediaElementScrapperTopic)
  val usersFollowing: KStream[String, String]   = builder.stream[String, String](userFollowingScrapperTopic)
  val followingElement: KStream[String, String] = builder.stream[String, String](followingElementScrapperTopic)
  val usersFollowers: KStream[String, String]   = builder.stream[String, String](userFollowersScrapperTopic)
  val followersElement: KStream[String, String] = builder.stream[String, String](followersElementScrapperTopic)
  val streams: KafkaStreams                     = new KafkaStreams(builder.build(), props)

//  users.selectKey()
  streams.start()

  sys.ShutdownHookThread {
    streams.close()
  }
}
