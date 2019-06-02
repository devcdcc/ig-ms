package com.github.devcdcc.queue

trait Publisher {
  def send[T](message: T)
}
