package controllers.helps

import play.api.Configuration

trait TopicsHelper {
  def config: Configuration
  val userScrapperTopic          = config.get[String]("topics.scrapper.user")
  val userMediaScrapperTopic     = config.get[String]("topics.scrapper.userMedia")
  val userFollowingScrapperTopic = config.get[String]("topics.scrapper.userFollowing")
  val userFollowersScrapperTopic = config.get[String]("topics.scrapper.userFollowers")
}
