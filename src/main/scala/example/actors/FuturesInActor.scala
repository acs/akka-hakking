package example.actors

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout

class FuturesInActor extends Actor {

  def receive = {
    case _ =>
      println("Received a message")
      sender() ! "OK"
  }
}

object FuturesInActor extends App {

  val system = ActorSystem("futures")

  val actor = system.actorOf(Props[FuturesInActor], "actor")
  implicit val timeout = Timeout(1 seconds)
  // val future = ask(actor, "TestMsg")
  val future: Future[String] = ask(actor, "TestMsg").mapTo[String]
  val result = Await.result(future, timeout.duration).asInstanceOf[String]
  println(s"Result from que actor $result")
  system.terminate()
}
