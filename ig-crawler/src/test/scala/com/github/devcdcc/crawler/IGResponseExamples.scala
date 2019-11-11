package com.github.devcdcc.crawler

import better.files.File

trait IGResponseExamples {

  final val MEDIA_RESPONSE = scala.io.Source.fromResource("media_result.json").mkString
  final val MEDIA_RESPONSE_WITHOUT_NEXT_VALUE =
    scala.io.Source.fromResource("media_result_without_next_value.json").mkString

}
