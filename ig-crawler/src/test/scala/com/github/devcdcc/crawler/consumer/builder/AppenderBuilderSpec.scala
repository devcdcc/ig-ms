package com.github.devcdcc.crawler.consumer.builder

import com.github.devcdcc.crawler.TestMessages
import com.github.devcdcc.crawler.consumer.converters.request.AbstractRequestConverter
import com.github.devcdcc.domain
import com.github.devcdcc.domain.{MediaRequest, PathHelper, QueueRequest, UserRequest}
import com.github.devcdcc.helpers.TopicsHelper
import io.circe.{Json, JsonObject, Printer}
import io.circe.generic.auto._
import io.circe.syntax._
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.{MustMatchers, WordSpec}
import com.github.devcdcc.crawler.api._
import com.github.devcdcc.crawler.api.exception.NextElementNotFoundException

class AppenderBuilderSpec extends WordSpec with MustMatchers with TestMessages with MockitoSugar {

  private val builder: StreamsBuilder = new StreamsBuilder
  private val converters              = mock[List[AbstractRequestConverter[domain.QueueRequest]]]
  implicit val printer: Printer       = Printer.noSpaces.copy(dropNullValues = true)
  private val subject                 = new AppenderBuilder(builder = builder, converters = converters)
  "A AppenderBuilder" can {
    "invoke jsonToRequest" when {
      "message is valid QueueRequest" should {
        "return Right for MediaRequest" in {
          //given
          val expected: QueueRequest = MediaRequest("notNull")
          val json                   = expected.asJson
          //when
          val result = subject.jsonToRequest(json)
          //then
          result mustBe Right(expected)
        }
        "return Right for UserRequest" in {
          //given
          val expected: QueueRequest = UserRequest("notNull")
          val json                   = expected.asJson
          //when
          val result = subject.jsonToRequest(json)
          //then
          result mustBe Right(expected)
        }
      }
      "json is invalid QueueRequest" should {
        "return left of json" in {
          //given
          val json = io.circe.parser.parse("{}").toOption.head

          val expected = Left(json)
          //when
          val result = subject.jsonToRequest(json)
          //then
          result mustBe expected
        }

      }
    }
    "doRequest" when {
      "converter is not found" should {
        "return Left of NoSuchElementException" in {
          //given
          val original: QueueRequest = MediaRequest("id")
          val expected: QueueRequest = null
          val response: Json         = null

          //when
          when(
            converters.find(ArgumentMatchers.any[AbstractRequestConverter[domain.QueueRequest] => Boolean]())
          ) thenReturn None
          val result: Either[Throwable, Json] = subject.doRequest(original)

          //then
          assertThrows[NoSuchElementException] {
            throw result.swap.toOption.head
          }
        }
      }
    }
    "getNextRequest" when {
      "converter is not found" should {
        "return Left of NoSuchElementException" in {
          //given
          val original: QueueRequest = MediaRequest("id")
          val response: Json         = null

          //when
          when(converters.find(ArgumentMatchers.any())) thenReturn None
          val result: Either[Throwable, QueueRequest] = subject.getNextRequest(original, response)

          //then
          assertThrows[NextElementNotFoundException] {
            throw result.swap.toOption.head
          }
        }
      }
      "exists converter" should {
        "return QueueRequest with some next_value for non final element" in {
          pending
        }
        "return QueueRequest with None next_value for final element" in {
          pending
        }
      }
    }
  }
}
