package com.github.devcdcc.crawler.api

package object exception {

  type CrawlerException = Exception

  case class NextElementNotFoundException(message: String = "Element not found")    extends CrawlerException(message)
  case class InvalidRequestException(message: String = "Invalid Request Exception") extends CrawlerException(message)
}
