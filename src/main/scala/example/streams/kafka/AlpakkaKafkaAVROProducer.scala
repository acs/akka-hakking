package example.streams.kafka

import java.io.ByteArrayOutputStream
import java.nio.file.Paths
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ClosedShape
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.scaladsl.FileIO
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.sksamuel.avro4s.AvroOutputStream
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer

case class CountryCapital(country: String, capital: String)

object AlpakkaKafkaAVROProducer extends App {

  implicit val actorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher
  val topic = "alpakkaCountryCapitals"
  // implicit val mat = ActorMaterializer()

  val resource = getClass.getResource("/country-list.csv")
  val path = Paths.get(resource.toURI)
  val source = FileIO.fromPath(path)

  // A comma-separated list of host/port
  val bootstrapServers = actorSystem.settings.config.getString("kafka.bootstrap.servers")
  val config = actorSystem.settings.config.getConfig("akka.kafka.producer")

  // val flow1 = Flow[String].map(ByteString(_))
  val flow1 = CsvParsing.lineScanner()
  val flow2 = Flow[List[ByteString]].map(_.map(_.utf8String))
  val flow3 = Flow[List[String]].map(list => CountryCapital(list(0), list(1)))
  // Convert CountryCapital stream of objects into Avro Records
  val flow4 = Flow[CountryCapital].map { cc =>
    val baos = new ByteArrayOutputStream()
    val output = AvroOutputStream.binary[CountryCapital](baos)
    output.write(cc)
    output.close()
    val result = baos.toByteArray
    baos.close()
    result
  }

  // Write the CapitalCountries stream of bytes Avro Records to kafka
  val producerSettings = ProducerSettings(config, new ByteArraySerializer, new ByteArraySerializer)
    .withBootstrapServers(bootstrapServers)

  val flow5 = Flow[Array[Byte]].map{array =>
    new ProducerRecord[Array[Byte], Array[Byte]](topic, array)
  }
  val sink = Producer.plainSink(producerSettings)

  val graph: RunnableGraph[Future[Done]] = RunnableGraph.fromGraph(GraphDSL.create(sink) {
    implicit builder => sink =>
      import GraphDSL.Implicits._
      source ~> flow1 ~> flow2 ~> flow3 ~> flow4 ~> flow5 ~> sink
      ClosedShape
  })

  graph.run().onComplete(_ => {
    actorSystem.terminate()
  })
  Await.result(actorSystem.whenTerminated, Duration.Inf)
}
