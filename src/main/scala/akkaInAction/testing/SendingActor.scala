package akkaInAction.testing

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.Logging

object SendingActor {

  def props(receiver: ActorRef) = Props(new SendingActor(receiver))

  case class Event(id: Long)
  case class SortEvents(unsorted: Vector[Event])
  case class SortedEvents(sorted: Vector[Event])
}

class SendingActor(receiver:ActorRef) extends Actor {
  import SendingActor._

  val log = Logging(context.system, this)

  def receive = {
    case SortEvents(unsorted) =>
      receiver ! SortedEvents(unsorted.sortBy(_.id))
    case msg  => log.info(s"Actor does nothing $msg")
  }

}
