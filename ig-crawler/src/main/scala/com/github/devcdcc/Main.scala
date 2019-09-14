package com.github.devcdcc

import com.github.devcdcc.crawler.consumer.Orchestration
import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object Main extends App {
  sealed trait TestTrait
  sealed trait TestTrait2 {
    def xxx = ""
  }
  case class TestTrait1()                                    extends TestTrait
  case class TestCaseClass(value: String = "asdfff")         extends TestTrait
  case class TestCaseClass2(value: TestTrait = TestTrait1()) extends TestTrait2
  val x: TestTrait = TestCaseClass()
  println(x.asJson)
  val y: TestTrait2 = TestCaseClass2()
  println(y.asJson)
//  private val orchestration = new Orchestration()
//  orchestration.start()
}
