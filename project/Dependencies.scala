import sbt._


object Dependencies {

  val AkkaVersion = "2.5.32"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val akkaActors = "com.typesafe.akka" %% "akka-actor" % AkkaVersion
  lazy val alpakkaKafka = "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.5"
  lazy val alpakkaFile = "com.lightbend.akka" %% "akka-stream-alpakka-file" % "2.0.2"
  lazy val alpakkaCSV = "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.2"
  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream" % AkkaVersion
  lazy val avro4s ="com.sksamuel.avro4s" %% "avro4s-core" % "1.8.0"
}
