package com.github.devcdcc.services.queue.publishers

import java.util.Calendar

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.ProducerSettings
import com.github.devcdcc.services.queue._
import com.google.inject.Singleton
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
  * This is an implementation for [[Publisher]] class, that allow communicate
  * to kafka broker and publish messages.
  * @param keySerializer A [[K]] value used to initialize kafka kafka producer.
  * @param valueSerializer A [[V]] value used to initialize kafka kafka producer.
  * @param system actor systems that isolate the [[ExecutionContext]]
  * @param config configuration where run the producer, also contains the [[ExecutionContext]] where
  *               gonna runs the [[Future]] that is executed on [[sendAsync()]] method.
  * @param executionContext an [[ExecutionContext]] where gonna run this producer
  * @tparam K Type of key for kafka producer
  * @tparam V Type of Value for Kafka producer
  */
@Singleton
class KafkaPublisher[K, V](
    keySerializer: Serializer[K],
    valueSerializer: Serializer[V]
  )(implicit val system: ActorSystem = ActorSystem.create("kafka-producer",
        ConfigFactory.parseFile(new java.io.File("config/application.conf"))),
    private var config: Config = null,
    implicit private var executionContext: ExecutionContext = null)
    extends Publisher[K, V] {

  // setting executionContext if its null
  if (executionContext == null) executionContext = system.dispatcher
  // setting config if its null
  if (config == null) config = system.settings.config.getConfig("akka.kafka.producer")

  /**
    * [[ProducerSettings]] it's loads from application.conf File
    */
  private val producerSettings: ProducerSettings[K, V] =
    ProducerSettings(
      config,
      keySerializer,
      valueSerializer
    ).withBootstrapServers(config.getString("kafka-clients.bootstrap.servers"))

  /**
    * [[Producer]] that send messages to kafka
    */
  private val producer: org.apache.kafka.clients.producer.Producer[K, V] = producerSettings.createKafkaProducer()

  private def headerConverter(header: HeaderMessage) = new org.apache.kafka.common.header.Header {
    override def key(): String        = header._1
    override def value(): Array[Byte] = header._2
  }

  private def messageToProducerRecord[OV](message: Message[K, OV, V]): ProducerRecord[K, V] = message match {
    case Message(topic, _, key, partition, timesTamp, headers, _) =>
      new ProducerRecord[K, V](
        topic,
        partition,
        timesTamp,
        key,
        message.convert,
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
    * @param message this parameter have many fields that could be set to
    *                configure a message and send it to the publisher
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Future]] that returns the same message or maybe some fields could be modified by the
    *        protocol implementation.
    *        Actually kafka add the partition and offset message.
    */
  def sendAsync[OV](message: Message[K, OV, V]): Future[Message[K, OV, V]] = Future {
    val recordMetadata = producer.send(messageToProducerRecord(message)).get
    message.withOffset(recordMetadata.offset()).withPartition(recordMetadata.partition())
  }

  /**
    * Method used to send a sync message to kafka broker.
    * @param message this parameter have many fields that could be set to
    *                configure a message and send it to the publisher
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Either]][\[[Throwable]],[\[[Message]]] that could catch if the message was successful sent and return
    *        the same message or maybe some fields could be modified by the protocol implementation.
    *        Actually kafka add the partition and offset message.
    */
  def send[OV](message: Message[K, OV, V]): Either[Throwable, Message[K, OV, V]] =
    Try(producer.send(messageToProducerRecord(message)).get)
      .map(recordMetadata => message.withOffset(recordMetadata.offset()))
      .toEither

  /**
    * close the [[producer]] kafka connection.
    * @see [[org.apache.kafka.clients.producer.Producer]]
    * @return [[Either]][\[[Throwable]],[\[[Unit]]] that could catch connection was closed.
    */
  override def close: Either[Throwable, Unit] = Try(producer.close()).toEither

}

object TestingKafka extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  val producer                              = new KafkaPublisher(new StringSerializer, new StringSerializer)()
  implicit val simpleStringMessageConverter = new SimpleStringMessageConverter

  import scala.concurrent.duration._

  implicit private val duration: Duration = Duration(1000, MILLISECONDS)
  for (i <- 0 until 1)
    producer send (Message("test", Calendar.getInstance().getTimeInMillis.toString))
}
