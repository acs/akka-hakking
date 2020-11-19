package example.streams.kafka

import java.nio.file.Paths
import scala.concurrent.Future

import akka.Done
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.scaladsl.FileIO
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import akka.util.ByteString

case class CountryCapital(country: String, capital: String)

object AlpakkaKafkaAVROProducer extends App {

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  // implicit val mat = ActorMaterializer()

  val resource = getClass.getResource("/country-list.csv")
  val path = Paths.get(resource.toURI)
  val source = FileIO.fromPath(path)

  // A comma-separated list of host/port
  val bootstrapServers = system.settings.config.getString("kafka.bootstrap.servers")

  // val flow1 = Flow[String].map(ByteString(_))
  val flow2 = CsvParsing.lineScanner()
  val flow3 = Flow[List[ByteString]].map(_.map(_.utf8String))
  val flow4 = Flow[List[String]].map(list => CountryCapital(list(0), list(1)))
  val sink: Sink[Any, Future[Done]] = Sink.foreach(println)

  val graph: RunnableGraph[Future[Done]] = RunnableGraph.fromGraph(GraphDSL.create(sink) { implicit builder => sink =>
      import GraphDSL.Implicits._
      source ~> flow2 ~> flow3 ~> flow4 ~> sink
      ClosedShape
  })

  graph.run().onComplete(_ => {
    system.terminate()
  })
}
