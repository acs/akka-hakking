package example.streams.kafka

import scala.concurrent.Future

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer


object AlpakkaKafkaConsumer extends App {

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()

  // A comma-separated list of host/port
  val bootstrapServers = system.settings.config.getString("kafka.bootstrap.servers")

  val config = system.settings.config.getConfig("our-kafka-consumer")
  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId("group2")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val topic = "alpakkaString"
  val topicAvro = "alpakkaCountryCapitals"
  val topicCommands = "choreography_commands"

  val source = Consumer.committableSource(consumerSettings, Subscriptions.topics(topicCommands))

  val done: Future[Done] =
    source
      .map(_.toString)
      .runForeach(println)

  done.onComplete(_ => {
    println(s"All events have been read from $topicAvro in Kafka")
    system.terminate()
  })
}
