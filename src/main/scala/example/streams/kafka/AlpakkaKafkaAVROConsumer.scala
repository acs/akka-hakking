package example.streams.kafka

import java.io.ByteArrayInputStream
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage
import akka.kafka.ConsumerSettings
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.ClosedShape
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import com.sksamuel.avro4s.AvroInputStream
import com.sksamuel.avro4s.AvroSchema
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer


object AlpakkaKafkaAVROConsumer extends App {

  implicit val actorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher
  implicit val mat = ActorMaterializer()

  // A comma-separated list of host/port
  val bootstrapServers = actorSystem.settings.config.getString("kafka.bootstrap.servers")

  val config = actorSystem.settings.config.getConfig("our-kafka-consumer")
  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId("group2")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val topicAvro = "alpakkaCountryCapitals"
  val schema = AvroSchema[CountryCapital]

  val source = Consumer.committableSource(consumerSettings, Subscriptions.topics(topicAvro))
  val flow1 = Flow[ConsumerMessage.CommittableMessage[String, Array[Byte]]].map{
    msg => msg.record.value()}
  val flow2 = Flow[Array[Byte]].map{ array =>
    val bais = new ByteArrayInputStream(array)
    val is = AvroInputStream.binary[CountryCapital].from(bais).build(schema)
    is.iterator.toSeq.head
  }
  val sink = Sink.foreach[CountryCapital](println)

  val graph: RunnableGraph[Future[Done]] = RunnableGraph.fromGraph(GraphDSL.create(sink){ implicit builder =>
    s =>
      import GraphDSL.Implicits._
      source ~> flow1 ~> flow2 ~> s
      ClosedShape
  })

  graph.run().onComplete(_ => {
    actorSystem.terminate()
  })
  Await.result(actorSystem.whenTerminated, Duration.Inf)
}
