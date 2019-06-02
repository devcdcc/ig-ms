package com.github.devcdcc

import io.circe.{Json => CirceJson}
import play.api.libs.json.{JsValue => PlayJsValue, Json => PlayJson}
package object queue {

  type HeaderMessage = (String, Array[Byte])
  abstract case class Message[T, Y](
      topic: String,
      value: T,
      key: Option[Y] = None,
      partition: Option[Int] = None,
      timesTamp: Option[Long] = None,
      headers: Option[Iterable[HeaderMessage]] = None) {
    override def toString: String = value match {
      case json: CirceJson   => json.noSpaces
      case json: PlayJsValue => PlayJson.stringify(json)
      case any               => any.toString
    }
    def asBrokerMessageModel[T]
  }
}
