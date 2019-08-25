package com.github.devcdcc.crawler.consumer.builder.appender

import com.github.devcdcc.crawler.consumer.builder.BasicJsonStringBuilder
import com.github.devcdcc.crawler.consumer.helpers.TopicsHelper
import com.github.devcdcc.crawler.consumer.converters.media.MediaConverter
import org.apache.kafka.streams.scala.StreamsBuilder

class MediaAppenderBuilder(builder: StreamsBuilder, converters: List[MediaConverter])
    extends BasicJsonStringBuilder(builder = builder, topic = TopicsHelper.userMediaScrapperTopic) {}
