package com.github.devcdcc.crawler.consumer.requester

import com.github.devcdcc.domain.QueueRequest

trait AbstractRequester[A <: QueueRequest, B] {
  def doRequest(a: A): B
}
