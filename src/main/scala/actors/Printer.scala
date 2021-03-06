package actors

import actors.crawlers.ProviderResult
import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by ekalashnikov on 12/11/2016 AD.
  */
class Printer extends Actor with LazyLogging {
  override def receive: Receive = {
    case ProviderResult(provider, items) =>
      val message = items
        .sortBy(x => (x.price, -x.lastDate.getMillis))
        .map(x => s"${x.title}, ${x.price}, ${x.url}, ${x.lastDate}")
        .mkString("\n")

      logger.info(s"Found ${items.length} items from $provider:\n$message")
  }
}
