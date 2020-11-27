package akkaInAction

case class Event(
                  host: String,
                  service: String,
                  state: String,
                  time: String,
                  description: String,
                  tag: Option[String] = None,
                  metric: Option[Double] = None
                )

class EventFilter {

}

object EventFilter extends App {

  /*
  A simple command-line application, takes three arguments:
    * an input file containing the log events
    * an output file to write JSON formatted events to
    * the state of the events to filter on
  */

}
