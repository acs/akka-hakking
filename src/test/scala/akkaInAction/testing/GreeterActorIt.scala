package akkaInAction.testing

import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.CallingThreadDispatcher
import akka.testkit.EventFilter
import akka.testkit.TestKit
import akkaInAction.testing.GreeterActorIt.testSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.MustMatchers
import org.scalatest.WordSpecLike


class GreeterActorIt extends TestKit(testSystem)
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }

  "A Greeter Actor" must {
    "say Hello World! when a Greeting(\"World\") is sent to it" in {

      import GreeterActor._

      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[GreeterActor].withDispatcher(dispatcherId)
      val greeter = system.actorOf(props)
      EventFilter.info(message = "Hello World!",
        occurrences = 1).intercept {
        greeter ! Greeting("World")
      }

    }
  }
}

object GreeterActorIt {

  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        akka.loggers = [akka.testkit.TestEventListener]
      """)
    ActorSystem("testsystem", config)
  }
}
