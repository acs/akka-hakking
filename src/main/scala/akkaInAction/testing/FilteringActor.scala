package akkaInAction.testing

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.event.Logging


object FilteringActor {

  def props(nextActor: ActorRef, bufferSize: Int) = Props(new FilteringActor(nextActor, bufferSize))

  case class Event(id: Long)
}

class FilteringActor(nextActor:ActorRef, bufferSize: Int) extends Actor {
  import FilteringActor._
  var lastMessages = Vector[Event]()

  val log = Logging(context.system, this)

  def receive = {
    case msg: Event =>
      if (!lastMessages.contains(msg)) {
        lastMessages = lastMessages :+ msg
        nextActor ! msg
        if (lastMessages.size > bufferSize) {
          // discard de oldest
          lastMessages = lastMessages.tail
        }
      }

    case msg  => log.info(s"Actor does nothing $msg")
  }

}
