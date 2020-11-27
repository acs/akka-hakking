package akkaInAction.testing

import akka.actor.Actor
import akka.actor.ActorRef
import akka.event.Logging

object SilentActor {
  case class SilentMessage(data: String)
  case class GetState(receiver: ActorRef)
}

class SilentActor extends Actor {
  import SilentActor._
  var internalState = Vector[String]()

  val log = Logging(context.system, this)

  def receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data
    case GetState(receiver) => receiver ! state
    case msg  => log.info(s"Actor does nothing $msg")
  }

  def state = {
    internalState
  }

}
