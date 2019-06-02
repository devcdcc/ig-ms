package com.github.devcdcc

package object queue {

  type HeaderMessage = (String, Array[Byte])

  case class Message[T, Y](
      topic: String,
      value: T,
      key: Option[Y] = None,
      partition: Option[Int] = None,
      timesTamp: Option[Long] = None,
      headers: Option[Iterable[HeaderMessage]] = None)
}
