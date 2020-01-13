package com.github.devcdcc.crawler.consumer.builder

import com.github.devcdcc.crawler.consumer.OrchestrationTestTopology
import com.github.devcdcc.crawler.consumer.converters.request.{AbstractRequestConverter, MediaRequestConverter}
import com.github.devcdcc.crawler.{IGResponseExamples, TestMessages}
import com.github.devcdcc.domain.{MediaRequest, QueueRequest, UserRequest}
import com.github.devcdcc.helpers.TopicsHelper
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Json, Printer}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class AppenderBuilderSpec
    extends WordSpec
    with MustMatchers
    with TestMessages
    with MockitoSugar
    with IGResponseExamples {

  private val subjectBuilder: StreamsBuilder = new StreamsBuilder
  private val converters                     = mock[List[AbstractRequestConverter]]
  implicit val printer: Printer              = Printer.noSpaces.copy(dropNullValues = true)
  private val subject                        = new AppenderBuilder(builder = subjectBuilder, converters = converters)
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

          //when
          when(
            converters.find(ArgumentMatchers.any[AbstractRequestConverter => Boolean]())
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
      "converter exists and has a next value" should {
        "return right for MediaRequest " in {

          //given
          val original: QueueRequest = MediaRequest("id")
          val expected: Either[Throwable, QueueRequest] =
            Right(
              MediaRequest(
                userId = "id",
                next_max_id = Some("2115131715534378849_375222529"),
                hasNext = Some(true),
                None,
                None,
                None
              )
            )
          val response: Json = io.circe.parser.parse(MEDIA_RESPONSE).toOption.get

          val value: AbstractRequestConverter =
            new MediaRequestConverter
          //when
          when(
            converters.find(ArgumentMatchers.any[AbstractRequestConverter => Boolean]())
          ) thenReturn Some(value)
          val result = subject.getNextRequest(original, response)
          //then
          result.response mustBe expected
        }
        "return right for UserRequest" in pending
      }
      "converter exists and has't a next value" should {
        "return right for MediaRequest " in {

          //given
          val original: QueueRequest = MediaRequest("id")
          val expected: Either[Throwable, QueueRequest] =
            Right(MediaRequest("id", None, None, None, None, None))
          val response: Json = io.circe.parser.parse(MEDIA_RESPONSE_WITHOUT_NEXT_VALUE).toOption.get

          val value: AbstractRequestConverter =
            new MediaRequestConverter
          //when
          when(
            converters.find(ArgumentMatchers.any[AbstractRequestConverter => Boolean]())
          ) thenReturn Some(value)
          val result = subject.getNextRequest(original, response)
          //then
          assertThrows[NoSuchElementException] {
            throw result.response.swap.toOption.head
          }
        }
        "return right for UserRequest" in pending
      }
      "converter is not found" should {
        "return Left of NoSuchElementException" in {
          //given
          val original: QueueRequest = MediaRequest("id")
          val response: Json         = null

          //when
          when(converters.find(ArgumentMatchers.any())) thenReturn None
          val result = subject.getNextRequest(original, response)

          //then
          assertThrows[NoSuchElementException] {
            throw result.response.swap.toOption.head
          }
        }
      }
    }
    "transact AppenderBuilder" when {
      val factory =
        new ConsumerRecordFactory(TopicsHelper.appenderTopic, new StringSerializer, new StringSerializer)
      "have next value" should {
        "fill all necessary topics" in {
          pending
          // given
          val testTopology = new OrchestrationTestTopology {
            override val requestConverters: List[AbstractRequestConverter] = converters
          }
          val mediaRequestConverter: AbstractRequestConverter = mock[MediaRequestConverter]
          val requestOne: QueueRequest                        = MediaRequest("1")
          val responseOne: Json                               = io.circe.parser.parse(MEDIA_RESPONSE).toOption.get
          val requestTwo                                      = MediaRequest("id", Some("2115131715534378849_375222529"), Some(true), None, None, None)
          val responseTwo: Json                               = io.circe.parser.parse(MEDIA_RESPONSE_WITHOUT_NEXT_VALUE).toOption.get
          val requestThree                                    = requestTwo.copy(hasNext = Some(false))

          //when
          when(converters.find(ArgumentMatchers.any())) thenReturn Some(mediaRequestConverter)
          when(mediaRequestConverter.doRequest(requestOne)) thenReturn Right(responseOne)
          when(mediaRequestConverter.convert(requestOne, responseOne)) thenReturn Right(requestTwo)
          when(mediaRequestConverter.doRequest(requestTwo)) thenReturn Right(responseTwo)
          when(mediaRequestConverter.convert(requestTwo, responseTwo)) thenReturn Right(requestThree)
          testTopology.start()
          val topology = testTopology.kafkaStreams

          //should
          topology.pipeInput(factory.create(requestOne.asJson.noSpaces));
          (1 until 3).foreach { _ =>
            println(topology.readOutput(TopicsHelper.appenderTopic, new StringDeserializer, new StringDeserializer))
          }

        }
      }
    }
  }
}
