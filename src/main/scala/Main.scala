import actors.Supervisor
import actors.crawlers.Request
import akka.actor.{ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by ekalashnikov on 12/7/2016 AD.
  */
object Main extends App with LazyLogging {
  val system = ActorSystem("crawler")

  val supervisor = system.actorOf(Props[Supervisor])
  supervisor ! Request("CBR 250", 30000, 60000)
}
