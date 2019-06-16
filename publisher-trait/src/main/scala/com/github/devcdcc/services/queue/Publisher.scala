package com.github.devcdcc.services.queue

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try

trait Publisher[K, V] {

  /**
    * Method used to send a async message to the publisher API
    * @param message this parameter have many fields that could be set to configure a message and send it to the publisher
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Future]] that could catch later if the message was successful sent.
    */
  def sendAsync[OV](message: Message[K, OV, V]*): Future[Any]

  /**
    * Method used to send a sync message to the publisher API
    * @param message this parameter have many fields that could be set to configure a message and send it to the publisher
    * @tparam OV OV original message value that gonna be send, internally that value is converted to the target value.
    * @return [[Either]][\[[Throwable]],[\[[Unit]]] that could catch if the message was successful sent.
    */
  def send[OV](message: Message[K, OV, V]*)(implicit timeOut: Duration): Either[Throwable, Unit]

  /**
    * Close the publish connection.
    * @return [[Either]][\[[Throwable]],[\[[Unit]]] that could catch connection was closed.
    */
  def close: Either[Throwable, Unit] = Right(())
}
