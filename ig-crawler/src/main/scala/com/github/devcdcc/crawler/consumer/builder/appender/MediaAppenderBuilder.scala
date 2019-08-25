package com.github.devcdcc.crawler.consumer.builder.appender

import com.github.devcdcc.crawler.consumer.builder.BasicJsonStringBuilder
import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import com.github.devcdcc.crawler.consumer.converters.AbstractConverter
import com.github.devcdcc.crawler.consumer.converters.request.AbstractRequestConverter
import com.github.devcdcc.crawler.wrapper
import org.apache.kafka.streams.scala.StreamsBuilder

class MediaAppenderBuilder(builder: StreamsBuilder, converters: List[AbstractRequestConverter[wrapper.MediaRequest]])
    extends BasicJsonStringBuilder(builder = builder, topic = TopicsHelper.userMediaScrapperTopic) {}
