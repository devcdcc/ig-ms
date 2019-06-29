package com.github.devcdcc.services.queue.publishers

import java.util.Calendar

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.ProducerSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.github.devcdcc.services.queue._
import com.google.inject.Singleton
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}

import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  *
  * @param keySerializer
  * @param valueSerializer
  * @param system
  * @param config
  * @param executor an [[ExecutionContextExecutor]] where gonna run this producer
  * @tparam K1
  * @tparam V1
  */
@Singleton
class KafkaPublisher[K1, V1](
    keySerializer: Serializer[K1],
    valueSerializer: Serializer[V1]
  )(implicit val system: ActorSystem = ActorSystem.create("kafka-producer",
        ConfigFactory.parseFile(new java.io.File("config/application.conf"))),
    private var config: Config = null,
    implicit private var executor: ExecutionContextExecutor = null)
    extends Publisher[K1, V1] {

  // setting executor if its null
  if (executor == null) executor = system.dispatcher
  // setting config if its null
  if (config == null) config = system.settings.config.getConfig("akka.kafka.producer")

  /**
    * [[ActorMaterializer]] that gonna run send method.
    */
  implicit private val actorMaterializer: ActorMaterializer = ActorMaterializer()

  /**
    * [[ProducerSettings]] it's loads from application.conf File
    */
  private val producerSettings: ProducerSettings[K1, V1] =
    ProducerSettings(
      config,
      keySerializer,
      valueSerializer
    ).withBootstrapServers(config.getString("kafka-clients.bootstrap.servers"))

  /**
    * [[Producer]] that send messages to kafka
    */
  private val producer: org.apache.kafka.clients.producer.Producer[K1, V1] = producerSettings.createKafkaProducer()

  private def headerConverter(header: HeaderMessage) = new org.apache.kafka.common.header.Header {
    override def key(): String        = header._1
    override def value(): Array[Byte] = header._2
  }

  private def messageToProducerRecord[OV](message: Message[K1, OV, V1]): ProducerRecord[K1, V1] = message match {
    case Message(topic, _, key, partition, timesTamp, headers) =>
      new ProducerRecord[K1, V1](
        topic,
        partition,
        timesTamp,
        key,
        message.convertValue,
        if (headers == null)
          null
        else
          headers
            .map(headerConverter)
            .asJavaCollection
      )
  }

  /**
    * Method used to send a async messages to kafka broker.
    * @param message this parameter have many fields that could be set to configure a message and send it to the publisher
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Future]] that could catch later if the message was successful sent.
    */
  override def sendAsync[OV](message: Message[K1, OV, V1]*): Future[Done] = {
    val seq: Seq[ProducerRecord[K1, V1]] = message.map(messageToProducerRecord)
    val iterable: scala.collection.immutable.Iterable[ProducerRecord[K1, V1]] =
      scala.collection.immutable.Iterable(seq.toArray: _*)
    val source: Source[ProducerRecord[K1, V1], NotUsed] = Source[ProducerRecord[K1, V1]](iterable)
    source
      .runWith(Producer.plainSink(producerSettings, producer))
  }

  /**
    * Method used to send a sync message to kafka broker.
    * @param message this parameter have many fields that could be set to configure a message and send it to the publisher
    * @param timeOut
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Either]][\[[Throwable]],[\[[Unit]]] that could catch if the message was successful sent.
    */
  override def send[OV](message: Message[K1, OV, V1]*)(implicit timeOut: Duration): Either[Throwable, Unit] =
    Try(scala.concurrent.Await.result(this.sendAsync(message: _*), timeOut)).toEither.map(_ => ())

  /**
    * close the [[producer]] kafka connection.
    * @see [[org.apache.kafka.clients.producer.Producer]]
    * @return [[Either]][\[[Throwable]],[\[[Unit]]] that could catch connection was closed.
    */
  override def close: Either[Throwable, Unit] = Try(producer.close()).toEither

}

object TestingKafka extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  val p                                     = new KafkaPublisher(new StringSerializer, new StringSerializer)()
  implicit val simpleStringMessageConverter = new SimpleStringMessageValueConverter

  import scala.concurrent.duration._

  implicit private val duration: Duration = Duration(1000, MILLISECONDS)
  for (i <- 0 until 10)
    p send (Message("test", Calendar.getInstance().getTimeInMillis.toString))
}
