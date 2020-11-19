package example.streams

import java.nio.file.Paths
import scala.concurrent.Future

import akka.Done
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

class StreamsGame {

}

object Main extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val ec = system.dispatcher

  val source: Source[Int, NotUsed] = Source(1 to 100)

  val done: Future[Done] = source.runForeach(i => println(i))

  // done.onComplete(_ => system.terminate())

  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

  val result: Future[IOResult] =
    factorials.map(num => ByteString(s"$num\n")).runWith(FileIO.toPath(Paths.get("factorials.txt")))

  result.onComplete(_ => system.terminate())
}
