package com.github.devcdcc.services.queue

trait Publisher {
  def send[T](message: T)
}
