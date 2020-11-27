package akkaInAction.testing

import scala.util.Random

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import akka.testkit.TestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.MustMatchers
import org.scalatest.WordSpecLike


class SendingActorIt extends TestKit(ActorSystem("testsytem"))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }

  "A Sending Actor" must {
    "send a message to another actor when it has finished processing" in {
      import SendingActor._
      val props = SendingActor.props(testActor)
      val sendingActor = system.actorOf(props, "sendingActor")

      val size = 1000
      val maxInclusive = 1000000

      def randomEvents() = (0 until size).map { _ =>
        Event(Random.nextInt(maxInclusive))
      }.toVector

      val unsorted = randomEvents()
      sendingActor ! SortEvents(unsorted)

      expectMsgPF() {
        case SortedEvents(events) =>
          events.size mustBe size
          unsorted.sortBy(_.id) mustBe events
      }
    }
  }

}
