package com.github.devcdcc

import io.circe.Json

import Json._
package object queue {

  type HeaderMessage = (String, Array[Byte])
  case class Message[T, Y](
      topic: String,
      value: T,
      key: Option[Y] = None,
      partition: Option[Int] = None,
      timesTamp: Option[Long] = None,
      headers: Option[Iterable[HeaderMessage]] = None) {

    override def toString: String = value match {
      case json: Json => json.noSpaces
      case any        => any.toString
    }
  }
}
