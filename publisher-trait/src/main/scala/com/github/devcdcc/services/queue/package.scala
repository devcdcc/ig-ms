package com.github.devcdcc.services

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
    )(implicit _converter: MessageValueConverter[OV, V]) {

    def convertValue: V                                    = converter.valueConvert(this.value)
    def withOffset(offset: Long): Message[K, OV, V]        = this.copy(offset = Option(offset))
    def withPartition(partition: Int): Message[K, OV, V]   = this.copy(partition = partition)
    def withTimesStamp(timesTamp: Long): Message[K, OV, V] = this.copy(timesTamp = timesTamp)
    def converter: MessageValueConverter[OV, V]            = _converter
  }

  /**
    * [[MessageValueConverter]] is an trait that helps to define a conversion that gonna be used to
    * convert the message value to his destination value. For example, if you want to convert a Json to
    * an String you can define MessageConverter[Json, String] and inside convert method do the convert.
    *
    * @tparam OV Original value
    * @tparam DV Destination value target of message.
    */
  trait MessageValueConverter[OV, DV] {

    /**
      * Map a origin value [[OV]] to a destination value [[DV]]
      * @param value [[OV]].
      * @return returns an [[DV]] once [[OV]] is converted.
      */
    def valueConvert(value: OV): DV
  }

  class SimpleStringMessageValueConverter extends MessageValueConverter[String, String] {
    @inline override def valueConvert(value: String): String = value
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
