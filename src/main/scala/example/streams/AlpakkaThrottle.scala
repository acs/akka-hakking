package example.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Source
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime


class AlpakkaThrottle {

}

object AlpakkaThrottle extends App {
  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()

  val greetingFlow =
    Flow[String]
      .throttle(1, 5 seconds)
      .map { name => s"hello, $name" }

  Source
    .tick(
      initialDelay = 1 second,
      interval = 1 second,
      tick = "Gabriel"
    )
    .via(greetingFlow)
    .runForeach(println)
}
