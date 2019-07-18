package com.github.devcdcc.crawler.consumer.helpers

import com.typesafe.config.Config

trait TopicsHelper {
  def config: Config
  val userScrapperTopic: String             = config.getString("topics.scrapper.user")
  val userMediaScrapperTopic: String        = config.getString("topics.scrapper.userMedia")
  val mediaElementScrapperTopic: String     = config.getString("topics.scrapper.mediaElement")
  val userFollowingScrapperTopic: String    = config.getString("topics.scrapper.userFollowing")
  val followingElementScrapperTopic: String = config.getString("topics.scrapper.followingElement")
  val userFollowersScrapperTopic: String    = config.getString("topics.scrapper.userFollowers")
  val followersElementScrapperTopic: String = config.getString("topics.scrapper.followersElement")
}
