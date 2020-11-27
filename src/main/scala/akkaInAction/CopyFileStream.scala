package akkaInAction


import java.nio.file.Paths
import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.IOResult
import akka.stream.scaladsl.FileIO
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString


class CopyFileStream {

}

object CopyFileStream extends App {
  println("Sample to play with basic Akka Streams")

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val inResource = getClass.getResource("/country-list.csv")
  val inputFile = Paths.get(inResource.toURI)
  val outputFile = Paths.get("streams.csv")

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(inputFile)
  val sink: Sink[ByteString, Future[IOResult]] =  FileIO.toPath(outputFile)
  val runnableGraph: RunnableGraph[Future[IOResult]] =  source.to(sink)

  runnableGraph.run().flatMap { result =>
    println(s"${result.status}, ${result.count} bytes read.")
    system.terminate()
  }

}
