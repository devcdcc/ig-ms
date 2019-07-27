package com.github.devcdcc

import com.typesafe.config.{Config, ConfigFactory}

object Configuration {

  def config: Config = ConfigFactory.load()
}
