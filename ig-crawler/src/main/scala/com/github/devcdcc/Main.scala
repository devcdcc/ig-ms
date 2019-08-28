package com.github.devcdcc

import com.github.devcdcc.crawler.consumer.Orchestration

object Main extends App {
  private val orchestration = new Orchestration()
  orchestration.start()
}
