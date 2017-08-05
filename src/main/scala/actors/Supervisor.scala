package actors

import actors.crawlers._
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.duration._

/**
  * Created by ekalashnikov on 12/11/2016 AD.
  */
object Supervisor extends LazyLogging {
  implicit class futureWithRecoverLogging(resultFuture: Future[_]) {
    def recoverWithLog(provider: String)(implicit ec: ExecutionContext): Future[ProviderResult] = {
      resultFuture.mapTo[ProviderResult].recover {
        case e =>
          logger.error(s"Error while requesting $provider", e)
          ProviderResult(provider, Nil)
      }
    }
  }
}

class Supervisor extends Actor {
  import Supervisor._

  val translator: ActorRef = context.actorOf(Props[Translator], "translator")
  val printer: ActorRef = context.actorOf(Props[Printer], "printer")

  val kaideeCrawler: ActorRef = context.actorOf(Props(classOf[KaideeCrawler], translator), "kaidee")
  val craiglistCrawler: ActorRef = context.actorOf(Props(classOf[CraiglistCrawler]), "craiglist")

  implicit val timeout: Timeout = Timeout(10.seconds)
  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case r @ Request(query, min, max, _, sources) =>
      val caller = sender()

      val kaideeOF = if (sources.contains("kaidee")) {
        Some((kaideeCrawler ? r).recoverWithLog("kaidee") to printer)
      } else None

      val craiglistOF = if (sources.contains("craiglist")) {
        Some(Future.sequence {
          List(
            (craiglistCrawler ? r.copy(pageNumber = Some(1))).recoverWithLog("craiglist"),
            (craiglistCrawler ? r.copy(pageNumber = Some(2))).recoverWithLog("craiglist"),
            (craiglistCrawler ? r.copy(pageNumber = Some(3))).recoverWithLog("craiglist")
          )
        }.map(results => ProviderResult(results.head.provider, results.flatMap(_.items).distinct)) to printer)
      } else None

      val providers = Seq(kaideeOF, craiglistOF).flatten

      //  Combine results
      Future.sequence(providers.map(_.future)).map(Response) pipeTo caller
  }
}