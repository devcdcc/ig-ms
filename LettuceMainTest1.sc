#!/usr/local/bin/amm
import $ivy.`io.lettuce:lettuce-core:5.1.8.RELEASE`
import $ivy.`org.scala-lang.modules:scala-java8-compat_2.12:0.8.0`
import io.lettuce.core.RedisURI
import io.lettuce.core.RedisClient
import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global

val redisUri = RedisURI.Builder.redis("localhost").build()
val client = RedisClient.create(redisUri)
val connection = client.connect()
val commands = connection.async()
//  commands.setAutoFlushCommands(false)
commands.multi()
val f = commands.mget("operationBeenExecuted:28464383")
f.toScala.onComplete(println)
val g = commands.get("nrostrace:00120418:6:89762354:0")
g.toScala.onComplete(println)
val h = commands.mget("tiposactividad:5813")
commands.set("key","value")
h.toScala.onComplete(println)
println("running as transaction")
scala.io.StdIn.readLine()
commands.exec().toScala.onComplete(println)
scala.io.StdIn.readLine()
scala.io.StdIn.readLine()
//  commands.flushCommands()
//  scala.io.StdIn.readLine()