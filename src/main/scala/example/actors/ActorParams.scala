package example.actors

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props


class ActorParams(p:String, i:Int = 5) extends Actor {

  def receive = {
    case _ => println("Message received")

  }

}

object ActorParams extends App {

  def apply(p: String, i: Int): ActorParams = new ActorParams(p, i)
  def apply(p: String): ActorParams = new ActorParams(p, 5)

  val system: ActorSystem = ActorSystem()

  val actor = system.actorOf(Props(classOf[ActorParams], ""), "actorName")

  actor ! "Test"

  Thread.sleep(2)

  system.terminate()
}
