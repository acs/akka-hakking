package example.streams.kafka

import scala.concurrent.Future

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer


object AlpakkaKafkaProducer extends App {

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()

  // A comma-separated list of host/port
  val bootstrapServers = system.settings.config.getString("kafka.bootstrap.servers")

  val config = system.settings.config.getConfig("akka.kafka.producer")
  val producerSettings =
    ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)

  val topic = "alpakkaString"
  val done: Future[Done] =
    Source(1 to 100)
      .map(_.toString)
      .map(value => new ProducerRecord[String, String](topic, value))
      .runWith(Producer.plainSink(producerSettings))

  done.onComplete(_ => {
    println(s"Events have been added to $topic in Kafka")
    system.terminate()
  }
  )
}
