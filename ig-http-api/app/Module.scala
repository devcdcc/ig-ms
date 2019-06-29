import akka.actor.ActorSystem
import com.github.devcdcc.services.queue.Publisher
import com.github.devcdcc.services.queue.publishers.KafkaPublisher
import com.google.inject.{AbstractModule, Provides}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringSerializer

class Module extends AbstractModule {

  private val configFactory = ConfigFactory.load()

//  override def configure(): Unit =
//    bind(classOf[Publisher[String, String]])
//      .toInstance(new KafkaPublisher(new StringSerializer, new StringSerializer))

  @Provides lazy val publisher: Publisher[String, String] = {
    val actorSystem =
      ActorSystem.create("kafka-producer", ConfigFactory.parseFile(new java.io.File("config/application.conf")))
    new KafkaPublisher(new StringSerializer, new StringSerializer)(actorSystem)
  }

}
