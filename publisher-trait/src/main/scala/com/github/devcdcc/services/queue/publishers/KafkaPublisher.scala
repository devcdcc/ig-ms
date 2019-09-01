package com.github.devcdcc.services.queue.publishers

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import com.github.devcdcc.services.queue._
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.Serializer

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * This is an implementation for [[Publisher]] class, that allow communicate
  * to kafka broker and publish messages.
  * @param keySerializer A [[K]] value used to initialize kafka kafka producer.
  * @param valueSerializer A [[V]] value used to initialize kafka kafka producer.
  * @param system actor systems that isolate the [[ExecutionContext]]
  * @param config configuration where run the producer, also contains the [[ExecutionContext]] where
  *               gonna runs the [[Future]] that is executed on [[sendAsync()]] method.
  * @tparam K Type of key for kafka producer
  * @tparam V Type of Value for Kafka producer
  */
abstract class KafkaPublisher[K, V](
    keySerializer: Serializer[K],
    valueSerializer: Serializer[V],
    system: ActorSystem,
    private var config: Config = null)
    extends Publisher[K, V] {

  // setting executionContext if its null
  implicit val executionContext: ExecutionContext = system.dispatcher
  // setting config if its null
  if (config == null) config = system.settings.config.getConfig("akka.kafka.producer")

  /**
    * [[ProducerSettings]] it's loads from application.conf File
    */
  protected val producerSettings: ProducerSettings[K, V] =
    ProducerSettings(
      config,
      keySerializer,
      valueSerializer
    ).withBootstrapServers(config.getString("kafka-clients.bootstrap.servers"))

  /**
    * [[Producer]] that send messages to kafka
    */
  protected val producer: org.apache.kafka.clients.producer.Producer[K, V] = producerSettings.createKafkaProducer()

  private def mapHeader(header: HeaderMessage) = new org.apache.kafka.common.header.Header {
    override def key(): String        = header._1
    override def value(): Array[Byte] = header._2
  }

  private def headerConverter(headers: Iterable[HeaderMessage]) =
    if (headers == null)
      null
    else
      headers
        .map(mapHeader)
        .asJavaCollection

  private def messageToProducerRecord[OV](message: Message[K, OV, V]): ProducerRecord[K, V] = message match {
    case Message(topic, _, key, partition, timesTamp, headers, _) =>
      new ProducerRecord[K, V](
        topic,
        partition,
        timesTamp,
        key,
        message.convertValue,
        headerConverter(headers)
      )
  }

  /**
    * Simple method that fills original message with the [[RecordMetadata]] retrieved from send message
    * @param recordMetadata retrieved from kafka after send method
    * @param message original sent message
    * @tparam OV value type of message
    * @note Also could be represented as
    *       message
    *         .withOffset(recordMetadata.offset())
    *         .withPartition(recordMetadata.partition())
    *         .withTimesStamp(recordMetadata.timestamp())
    * @return a copy message with offset, partition and timestamp.
    */
  private def fillSentMessageWithRecordMetadata[OV](recordMetadata: RecordMetadata, message: Message[K, OV, V]) =
    message
      .withOffset(recordMetadata.offset())
      .withPartition(recordMetadata.partition())
      .withTimesStamp(recordMetadata.timestamp())

  /**
    * Method used to send a async messages to kafka broker.
    * @param message this parameter have many fields that could be set to
    *                configure a message and send it to the publisher
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Future]] that returns the same message or maybe some fields could be modified by the
    *        protocol implementation.
    *        Actually kafka add the partition and offset message.
    */
  def sendAsync[OV](message: Message[K, OV, V]): Future[Message[K, OV, V]] =
    Future(producer.send(messageToProducerRecord(message)).get)
      .map(recordMetadata => fillSentMessageWithRecordMetadata(recordMetadata, message))

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
      .map(recordMetadata => fillSentMessageWithRecordMetadata(recordMetadata, message))
      .toEither

  /**
    * close the [[producer]] kafka connection.
    * @see [[org.apache.kafka.clients.producer.Producer]]
    * @return [[Either]][\[[Throwable]],[\[[Unit]]] that could catch connection was closed.
    */
  override def close: Either[Throwable, Unit] = Try(producer.close()).toEither

}

//object TestingKafka extends App {
//  import scala.concurrent.ExecutionContext.Implicits.global
//
//  val producer = new KafkaPublisher(
//    new StringSerializer,
//    new StringSerializer,
//    ActorSystem.create("kafka-producer", ConfigFactory.parseFile(new java.io.File("config/application.conf")))
//  )
//  implicit val simpleStringMessageConverter = new SimpleStringMessageValueConverter
//
//  import scala.concurrent.duration._
//
//  implicit private val duration: Duration = Duration(1000, MILLISECONDS)
//  for (i <- 0 until 1)
//    producer send (Message("test", Calendar.getInstance().getTimeInMillis.toString))
//}
