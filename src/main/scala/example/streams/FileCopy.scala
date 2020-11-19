package example.streams

import java.nio.file.Paths
import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.IOResult
import akka.stream.scaladsl.FileIO
import akka.stream.scaladsl.GraphDSL.Implicits.SourceArrow
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString


class FileCopy {

}

object FileCopy extends App {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  // implicit val mat = ActorMaterializer()

  val resource = getClass.getResource("/country-list.csv")
  val path = Paths.get(resource.toURI)
  val outputFile = Paths.get("/tmp/stream.csv")

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(path)
  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(outputFile)
  val sink1 = Sink.foreach(println)
  val runnableGraph: RunnableGraph[Future[IOResult]] = source.to(sink)

  runnableGraph.run().flatMap { res =>
    println(s"Total bytes copied ${res.count}")
    system.terminate()
  }
}
