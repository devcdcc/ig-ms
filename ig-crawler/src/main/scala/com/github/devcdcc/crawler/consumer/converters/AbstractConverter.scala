package com.github.devcdcc.crawler.consumer.converters

trait AbstractConverter[A, B, C] {
  def elementType: A
  def isRequiredType(input: B): Boolean
  def convert: C
}
