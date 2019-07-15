package com.github.devcdcc.crawler.consumer.helpers

import com.typesafe.config.Config

trait TopicsHelper {
  def config: Config
  val userScrapperTopic          = config.getString("topics.scrapper.user")
  val userMediaScrapperTopic     = config.getString("topics.scrapper.userMedia")
  val userFollowingScrapperTopic = config.getString("topics.scrapper.userFollowing")
  val userFollowersScrapperTopic = config.getString("topics.scrapper.userFollowers")
}
