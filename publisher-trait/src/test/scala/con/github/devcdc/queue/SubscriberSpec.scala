package con.github.devcdc.queue

import org.scalatest._

class SubscriberSpec extends FlatSpec with Matchers {
//  val subject: Subscriber = new Subscriber()

}
import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object WordCountApplication extends App {
  import Serdes._

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9292")
    p
  }

  val builder: StreamsBuilder            = new StreamsBuilder
  val textLines: KStream[String, String] = builder.stream[String, String]("test")

  val wordCounts: KTable[String, Long] = textLines
    .flatMapValues(textLine => textLine.toLowerCase.split(""))
    .groupBy((_, word) => word)
    .count()(Materialized.as("counts-store"))
  wordCounts.toStream.foreach((wc, lng) => println(wc, lng))
  wordCounts.toStream.to("test2")

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()

//  sys.ShutdownHookThread {
//    streams.close(10, TimeUnit.SECONDS)
//  }
}
