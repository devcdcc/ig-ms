package com.github.devcdcc.services.queue.messages

import com.github.devcdcc.services.queue.Message

package object scrapper {

  case class IGUser[T, Y](override val value: T)  extends Message(topic = "ig:scrapper:user", value = value)
  case class IGMedia[T, Y](override val value: T) extends Message(topic = "ig:scrapper:media", value = value)

}
