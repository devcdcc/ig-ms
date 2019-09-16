package com.github.devcdcc.crawler.consumer.converters.request

import java.util.concurrent.Executors

import cats.effect.IO
import com.github.devcdcc.crawler.consumer.converters.AbstractConverter
import com.github.devcdcc.crawler.consumer.requester.AbstractRequester
import com.github.devcdcc.domain.QueueRequest
import com.typesafe.config.ConfigFactory
import io.circe.Json
import org.http4s.{Query, Uri}
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.circe.CirceEntityDecoder._
import scala.concurrent.ExecutionContext

abstract class AbstractRequestConverter[A <: QueueRequest]
    extends AbstractConverter[Option[String], A, (A, Json) => Either[Throwable, A]]
    with AbstractRequester[A, Either[Throwable, Json]] {
  implicit val ec: ExecutionContext = AbstractRequestConverter.ec
  val httpClient: Client[IO]        = AbstractRequestConverter.httpClient

  override def isRequiredType(input: A): Boolean = input.requestType == elementType

  override def doRequest(a: A): Either[Throwable, Json] = {
    val query = Query.empty :+ ("next_max_id", a.next_max_id)
    val uri   = Uri(path = a.requestURl, query = query)
    httpClient.expect[Json](uri).attempt.unsafeRunSync()
  }
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
