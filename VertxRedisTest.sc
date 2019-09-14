#!/usr/local/bin/amm
import $ivy.`io.vertx:vertx-redis-client-scala_2.12:3.8.0`
import io.vertx.scala.core.Vertx
import io.vertx.scala.redis.client.{Redis, RedisAPI, RedisOptions}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object VertxRedisMainTest extends App {
  var vertx      = Vertx.vertx()
  val connection = Redis.createClient(vertx, RedisOptions()).connectFuture()
  connection
    .map(RedisAPI.api)
    .map(api => {
      api.multiFuture().foreach(println)
      api.mgetFuture(mutable.Buffer("operationBeenExecuted:28464383")).onComplete(println)
      api.getFuture("nrostrace:00120418:6:89762354:0").onComplete(println)
      api.mgetFuture(mutable.Buffer("tiposactividad:5813")).onComplete(println)
      api.setFuture(mutable.Buffer("asdf", "value")).onComplete(println)
      println("before call to exec")
      scala.io.StdIn.readLine("Press Enter to continue\n")
      api.execFuture().foreach(println)
      println("after exec call")
    })
}
VertxRedisMainTest.main(null)
Thread.sleep(Int.MaxValue)