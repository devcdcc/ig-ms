package com.github.devcdcc.crawler.consumer.converters

trait AbstractConverter[A, B, C] {
  def elementType: A
  def convert: B => C
  def isRequiredType(input: B): Boolean
}
