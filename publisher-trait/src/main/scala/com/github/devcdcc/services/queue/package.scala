package com.github.devcdcc.services
import org.mongodb.scala.bson._
//import io.circe.{Json => CirceJson}
//import play.api.libs.json.{JsValue => PlayJsValue, Json => PlayJson}
package object queue {

  type HeaderMessage = (String, Array[Byte])
  case class Message[K, OV, V](
      topic: String,
      value: OV,
      key: K = null,
      partition: java.lang.Integer = null,
      timesTamp: java.lang.Long = null,
      headers: Iterable[HeaderMessage] = null,
      offset: Option[Long] = None
    )(implicit converter: MessageConverter[OV, V]) {

    def convert: V                                       = converter.convert(this.value)
    override def toString: String                        = converter.stringify
    def withOffset(offset: Long): Message[K, OV, V]      = this.copy(offset = Option(offset))
    def withPartition(partition: Int): Message[K, OV, V] = this.copy(partition = partition)
  }

  trait MessageConverter[V, T] {
    def convert(value: V): T
    def stringify: String = super.toString
  }

  class SimpleStringMessageConverter extends MessageConverter[String, String] {
    override def convert(value: String): String = value
  }

}
/*
import language.dynamics
import scala.collection.mutable.HashMap

class A extends Dynamic {
  private val map = new HashMap[String, Any]

  def selectDynamic(name: String): Any =
    return map.get(name)

  def updateDynamic(name: String)(value: Any) =
    map(name) = value
}

object TestDynamic extends App {
  val a = new A
  println(a.x)
}
 */
