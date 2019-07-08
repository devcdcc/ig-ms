import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import com.github.devcdcc.services.queue.Publisher
import com.github.devcdcc.services.queue.publishers.KafkaPublisher
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.typesafe.config.{Config, ConfigFactory}
import javax.inject.Inject
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import play.inject.DelegateApplicationLifecycle

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

class Module extends AbstractModule {

  @Singleton
  private class KafkaPublisherSimpleStringImplementation
      extends KafkaPublisher(
        new StringSerializer,
        new StringSerializer,
        system,
        config.getConfig("akka.kafka.producer")
      )

  private val kafkaInstance = new KafkaPublisherSimpleStringImplementation
  private val logger        = play.api.Logger(Module.super.toString)
  override def configure(): Unit =
    bind(classOf[Publisher[String, String]])
      .toInstance(kafkaInstance)

  //    ActorSystem.create("kafka-producer", ConfigFactory.parseFile(new java.io.File("config/application.conf")))
  lazy val config = ConfigFactory.load()
  lazy val system = ActorSystem.create("kafka-producer", config)

  sys.addShutdownHook {
    kafkaInstance.close match {
      case Left(fail) => play.api.Logger(Module.super.toString).error("Error trying to terminate actor system", fail)
      case _          =>
    }
    val shutdownHook = Try(Await.result(system.terminate(), Duration.create(60, TimeUnit.SECONDS))).toEither
    shutdownHook match {
      case Left(failure) => logger.error("Error trying to terminate actor system", failure)
      case Right(terminated) =>
        logger.info(s"Actor system  for kafka publisher ends: ${terminated.existenceConfirmed}.")
    }
  }

//  private val publisher: Publisher[String, String] = {
//    val actorSystem =
//      ActorSystem.create("kafka-producer", ConfigFactory.parseFile(new java.io.File("config/application.conf")))
//    new KafkaPublisher(new StringSerializer, new StringSerializer, actorSystem)
//  }

}
