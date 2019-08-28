package com.github.devcdcc.crawler.consumer.converters.request

import java.util.concurrent.Executors

import cats.effect.IO
import com.github.devcdcc.crawler.consumer.converters.AbstractConverter
import com.github.devcdcc.crawler.consumer.requester.AbstractRequester
import com.github.devcdcc.crawler.wrapper.QueueRequest
import com.typesafe.config.ConfigFactory
import io.circe.Json
import org.http4s.client.{Client, JavaNetClientBuilder}
import scala.concurrent.ExecutionContext

abstract class AbstractRequestConverter[A <: QueueRequest, B]
    extends AbstractConverter[Option[String], A, (A, Json) => A]
    with AbstractRequester[A, B] {
  implicit val ec: ExecutionContext              = AbstractRequestConverter.ec
  val httpClient: Client[IO]                     = AbstractRequestConverter.httpClient
  override def isRequiredType(input: A): Boolean = input.requestType == elementType
}

private object AbstractRequestConverter {
  import cats.effect._
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(
    Executors.newFixedThreadPool(ConfigFactory.load().getInt("request.parallelism"))
  )
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO]     = IO.timer(ec)
  val httpClient: Client[IO]        = JavaNetClientBuilder[IO](ec).create
}
