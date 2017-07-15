package actors

import actors.crawlers.{CraiglistCrawler, KaideeCrawler, Request, Result}
import akka.actor.{Actor, Props}
import akka.util.Timeout
import akka.pattern.ask
import akka.pattern.pipe

import scala.concurrent.Future
import scala.concurrent.duration._
/**
  * Created by ekalashnikov on 12/11/2016 AD.
  */
class Supervisor extends Actor {
  val translator = context.actorOf(Props[Translator], "translator")
  val printer = context.actorOf(Props[Printer], "printer")

  val kaideeCrawler = context.actorOf(Props(classOf[KaideeCrawler], translator), "kaidee")
  val craiglistCrawler = context.actorOf(Props(classOf[CraiglistCrawler]), "craiglist")

  implicit val timeout = Timeout(20 seconds)
  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case r @ Request(query, min, max, _) =>
      kaideeCrawler ? r pipeTo printer

      Future.sequence(List(
        (craiglistCrawler ? r.copy(pageNumber = Some(1))).mapTo[Result],
        (craiglistCrawler ? r.copy(pageNumber = Some(2))).mapTo[Result].recover { case _ => Result(Nil) },
        (craiglistCrawler ? r.copy(pageNumber = Some(3))).mapTo[Result].recover { case _ => Result(Nil) }
      )).map(results => Result(results.flatMap(_.items).distinct)) pipeTo printer
  }
}
