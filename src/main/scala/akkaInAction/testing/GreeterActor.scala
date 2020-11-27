package akkaInAction.testing

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.Logging

object GreeterActor {
  def props() = Props(new GreeterActor())

  case class Greeting(message: String)
}

class GreeterActor extends Actor with ActorLogging {
  import GreeterActor._

  def receive = {
    case Greeting(msg) => log.info(s"Hello $msg!")
    case msg  => log.info(s"Actor does nothing $msg")
  }

}
