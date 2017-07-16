import actors.Supervisor
import actors.crawlers.Request
import akka.actor.{ActorSystem, Props}
import api.Api
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by ekalashnikov on 12/7/2016 AD.
  */
object Main extends App with LazyLogging {
  val system = ActorSystem("crawler")
  Api.init(system)
}