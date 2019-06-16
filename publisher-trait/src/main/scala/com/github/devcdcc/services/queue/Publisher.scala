package com.github.devcdcc.services.queue

import akka.Done

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * this trait is an interface abstraction that allow a simple and unified way of communication to the
  * publish, it could be used for communicate with a message broker, it usually is used for micro services
  * communication.
  * @tparam K type of key for the message that is send.
  * @tparam V type of value for the message that is send.
  */
trait Publisher[K, V] {

  /**
    * Method used to send a async message to the publisher API.
    * @param message this parameter have many fields that could be set to
    *                configure a message and send it to the publisher
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Future]] that returns the same message or maybe some fields could be modified
    *        by the protocol implementation.
    */
//  def sendAsync[OV](message: Message[K, OV, V])(executionContext: ExecutionContext): Future[Message[K, OV, V]]

  def sendAsync[OV](message: Message[K, OV, V]): Future[Message[K, OV, V]]

  /**
    * Method used to send a sync message to the publisher API
    * @param message this parameter have many fields that could be set to
    *                configure a message and send it to the publisher
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Either]][\[[Throwable]],[\[[Message]]] that could catch if the message was successful sent and return
    *        the same message or maybe some fields could be modified by the protocol implementation.
    */
  def send[OV](message: Message[K, OV, V]): Either[Throwable, Message[K, OV, V]]

  /**
    * Close the publish connection.
    * @return [[Either]][\[[Throwable]],[\[[Unit]]] that could catch connection was closed.
    */
  def close: Either[Throwable, Unit] = Right(())
}
