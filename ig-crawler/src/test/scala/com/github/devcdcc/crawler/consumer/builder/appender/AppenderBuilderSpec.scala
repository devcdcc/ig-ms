package com.github.devcdcc.crawler.consumer.builder.appender

import com.github.devcdcc.crawler.consumer.builder.AppenderBuilder
import com.github.devcdcc.crawler.TestMessages
import com.github.devcdcc.crawler.consumer.converters.request.AbstractRequestConverter
import com.github.devcdcc.domain
import org.apache.kafka.streams.scala.StreamsBuilder
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.mockito.MockitoSugar

class AppenderBuilderSpec extends WordSpec with MustMatchers with TestMessages with MockitoSugar {

  val builder: StreamsBuilder = new StreamsBuilder
  private val converters      = List.empty[AbstractRequestConverter[domain.MediaRequest]]
  private val subject         = new AppenderBuilder(builder = builder, converters = converters)
  "AppenderBuilderSpec" when {
    "message is valid json" should {
      "return retrieve null for empty json" in {
        //given
        val expected = null

        //when
        val result = ""

        //then
//        result mustBe expected
        pending
      }
    }
  }
}
