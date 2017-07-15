package actors

import actors.Translator.Translate
import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import dispatch._
import Defaults._
import org.json4s.jackson.JsonMethods._

/**
  * Created by ekalashnikov on 12/8/2016 AD.
  */
object Translator {
  case class Translate(text: String, fromLanguage: Option[String], toLanguage: String)
}

class Translator extends Actor with LazyLogging {
  override def receive: Receive = {
    case Translate(text, _, _) =>
      val request = url("http://www.frengly.com/frengly/data/translate/")
          .POST
          .setBody(s"""{"text":"$text","srcLang":"Thai","destLang":"English","detected":" Thai"}""")
          .setContentType("text/json", "UTF-8")

      for (result <- Http(request > as.String)) {
        logger.info(result)
      }

    case Translate(text, None, to) =>
      val q = """q=%3Ca%20i%3D0%3E%E0%B8%98%E0%B8%B1%E0%B8%8D%E0%B8%9A%E0%B8%B8%E0%B8%A3%E0%B8%B5%3C%2Fa%3E%3Ca%20i%3D1%3E%E0%B8%88.%E0%B8%9B%E0%B8%97%E0%B8%B8%E0%B8%A1%E0%B8%98%E0%B8%B2%E0%B8%99%E0%B8%B5%3C%2Fa%3E&q=21%20%E0%B8%9E.%E0%B8%A2.%202559%2011%3A18%20%E0%B8%99.&q=%E0%B9%80%E0%B8%A1%E0%B8%99%E0%B8%B9&q=Evgeniy%20Kalashnikov&q=%E0%B8%AB%E0%B8%99%E0%B9%89%E0%B8%B2%E0%B9%81%E0%B8%A3%E0%B8%81&q=%E0%B8%A1%E0%B8%B8%E0%B8%A1%E0%B8%AA%E0%B8%A1%E0%B8%B2%E0%B8%8A%E0%B8%B4%E0%B8%81&q=%E0%B8%9B%E0%B8%A3%E0%B8%B0%E0%B8%A7%E0%B8%B1%E0%B8%95%E0%B8%B4%E0%B8%9A%E0%B8%A3%E0%B8%B4%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B8%9E%E0%B8%B4%E0%B9%80%E0%B8%A8%E0%B8%A9&q=%E0%B9%84%E0%B8%82%E0%B9%88%20Kaidee%20Egg&q=%E0%B8%9E%E0%B8%B9%E0%B8%94%E0%B8%84%E0%B8%B8%E0%B8%A2%20&q=%E0%B8%A3%E0%B8%B2%E0%B8%A2%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B9%82%E0%B8%9B%E0%B8%A3%E0%B8%94&q=%E0%B8%A3%E0%B8%B2%E0%B8%A2%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B8%97%E0%B8%B5%E0%B9%88%E0%B8%94%E0%B8%B9%E0%B8%A5%E0%B9%88%E0%B8%B2%E0%B8%AA%E0%B8%B8%E0%B8%94&q=%E0%B8%8A%E0%B9%88%E0%B8%A7%E0%B8%A2%E0%B9%80%E0%B8%AB%E0%B8%A5%E0%B8%B7%E0%B8%AD&q=%E0%B8%AD%E0%B8%AD%E0%B8%81%E0%B8%88%E0%B8%B2%E0%B8%81%E0%B8%A3%E0%B8%B0%E0%B8%9A%E0%B8%9A&q=GPX%20DEMON%20%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015%20%E0%B8%A3%E0%B8%B9%E0%B8%9B%E0%B8%97%E0%B8%B5%E0%B9%88%200&q=GPX%20DEMON%20%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015%20%E0%B8%A3%E0%B8%B9%E0%B8%9B%E0%B8%97%E0%B8%B5%E0%B9%88%201&q=GPX%20DEMON%20%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015%20%E0%B8%A3%E0%B8%B9%E0%B8%9B%E0%B8%97%E0%B8%B5%E0%B9%88%202&q=GPX%20DEMON%20%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015%20%E0%B8%A3%E0%B8%B9%E0%B8%9B%E0%B8%97%E0%B8%B5%E0%B9%88%203&q=GPX%20DEMON%20%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015%20%E0%B8%A3%E0%B8%B9%E0%B8%9B%E0%B8%97%E0%B8%B5%E0%B9%88%204&q=GPX%20DEMON%20%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015%20%E0%B8%A3%E0%B8%B9%E0%B8%9B%E0%B8%97%E0%B8%B5%E0%B9%88%205&q=GPX%20DEMON%20%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015%20%E0%B8%A3%E0%B8%B9%E0%B8%9B%E0%B8%97%E0%B8%B5%E0%B9%88%206&q=GPX%20DEMON%20%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015%20%E0%B8%A3%E0%B8%B9%E0%B8%9B%E0%B8%97%E0%B8%B5%E0%B9%88%207&q=%E0%B9%80%E0%B8%81%E0%B9%87%E0%B8%9A%E0%B9%84%E0%B8%A7%E0%B9%89%E0%B9%80%E0%B8%9B%E0%B9%87%E0%B8%99%E0%B8%A3%E0%B8%B2%E0%B8%A2%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B9%82%E0%B8%9B%E0%B8%A3%E0%B8%94&q=Kaidee%20%7C%7C%20GPX%20DEMON%20%E0%B8%AA%E0%B8%B5%E0%B9%81%E0%B8%94%E0%B8%87%20%E0%B8%9B%E0%B8%B5%202015&q=%E0%B9%80%E0%B8%A1%E0%B8%99%E0%B8%B9%E0%B8%AA%E0%B8%A1%E0%B8%B2%E0%B8%8A%E0%B8%B4%E0%B8%81&q=%E0%B8%A3%E0%B8%B2%E0%B8%A2%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B8%9B%E0%B8%A3%E0%B8%B0%E0%B8%81%E0%B8%B2%E0%B8%A8&q=%E0%B8%9B%E0%B8%A3%E0%B8%B0%E0%B8%81%E0%B8%B2%E0%B8%A8%E0%B8%97%E0%B8%B5%E0%B9%88%E0%B8%94%E0%B8%B9%E0%B8%A5%E0%B9%88%E0%B8%B2%E0%B8%AA%E0%B8%B8%E0%B8%94&q=Accesskey%20Help&q=%E0%B8%9E%E0%B8%B9%E0%B8%94%E0%B8%84%E0%B8%B8%E0%B8%A2&q=%E0%B8%A1%E0%B8%AD%E0%B9%80%E0%B8%95%E0%B8%AD%E0%B8%A3%E0%B9%8C%E0%B9%84%E0%B8%8B%E0%B8%84%E0%B9%8C&q=%E0%B8%A2%E0%B8%B5%E0%B9%88%E0%B8%AB%E0%B9%89%E0%B8%AD%E0%B8%AD%E0%B8%B7%E0%B9%88%E0%B8%99&q=%3Ca%20i%3D0%3Eprev%3C%2Fa%3E%3Ca%20i%3D1%3Enext%3C%2Fa%3E&q=36%2C000&q=%E0%B8%A3%E0%B8%B2%E0%B8%84%E0%B8%B2%E0%B8%A3%E0%B8%A7%E0%B8%A1%E0%B8%A1%E0%B8%B9%E0%B8%A5%E0%B8%84%E0%B9%88%E0%B8%B2%E0%B8%82%E0%B8%AD%E0%B8%87%E0%B9%81%E0%B8%96%E0%B8%A1%E0%B9%81%E0%B8%A5%E0%B9%89%E0%B8%A7%20(%E0%B8%96%E0%B9%89%E0%B8%B2%E0%B8%A1%E0%B8%B5)&q=%E0%B8%81%E0%B8%94%E0%B8%94%E0%B8%B9%E0%B9%80%E0%B8%9A%E0%B8%AD%E0%B8%A3%E0%B9%8C%E0%B9%82%E0%B8%97%E0%B8%A3%E0%B8%9C%E0%B8%B9%E0%B9%89%E0%B8%82%E0%B8%B2%E0%B8%A2&q=0869987458&q=%3Ca%20i%3D0%3E%E0%B8%82%E0%B8%93%E0%B8%B0%E0%B8%99%E0%B8%B5%E0%B9%89%E0%B9%80%E0%B8%A3%E0%B8%B2%E0%B8%81%E0%B8%B3%E0%B8%A5%E0%B8%B1%E0%B8%87%E0%B8%97%E0%B8%94%E0%B8%AA%E0%B8%AD%E0%B8%9A%E0%B9%80%E0%B8%9E%E0%B8%B7%E0%B9%88%E0%B8%AD%E0%B8%9E%E0%B8%B1%E0%B8%92%E0%B8%99%E0%B8%B2%E0%B8%A3%E0%B8%B0%E0%B8%9A%E0%B8%9A%20%E0%B8%84%E0%B8%B8%E0%B8%93%E0%B8%AA%E0%B8%B2%E0%B8%A1%E0%B8%B2%E0%B8%A3%E0%B8%96%E0%B9%82%E0%B8%97%E0%B8%A3%E0%B9%80%E0%B8%9A%E0%B8%AD%E0%B8%A3%E0%B9%8C%E0%B8%99%E0%B8%B5%E0%B9%89%E0%B9%84%E0%B8%94%E0%B9%89%E0%B8%A0%E0%B8%B2%E0%B8%A2%E0%B9%83%E0%B8%99%3C%2Fa%3E%3Ca%20i%3D1%3E%2005%3A00%20%3C%2Fa%3E%3Ca%20i%3D2%3E%E0%B8%99%E0%B8%B2%E0%B8%97%E0%B8%B5%20%E0%B9%80%E0%B8%A3%E0%B8%B2%E0%B8%88%E0%B8%B0%E0%B9%82%E0%B8%AD%E0%B8%99%E0%B8%AA%E0%B8%B2%E0%B8%A2%E0%B9%84%E0%B8%9B%E0%B8%A2%E0%B8%B1%E0%B8%87%E0%B9%80%E0%B8%88%E0%B9%89%E0%B8%B2%E0%B8%82%E0%B8%AD%E0%B8%87%E0%B8%AA%E0%B8%B4%E0%B8%99%E0%B8%84%E0%B9%89%E0%B8%B2%E0%B9%82%E0%B8%94%E0%B8%A2%E0%B8%AD%E0%B8%B1%E0%B8%95%E0%B9%82%E0%B8%99%E0%B8%A1%E0%B8%B1%E0%B8%95%E0%B8%B4%3C%2Fa%3E%3Ca%20i%3D3%3E%E0%B9%80%E0%B8%9E%E0%B8%B4%E0%B9%88%E0%B8%A1%E0%B9%80%E0%B8%95%E0%B8%B4%E0%B8%A1%3C%2Fa%3E&q=%E0%B8%AB%E0%B8%B2%E0%B8%81%E0%B8%88%E0%B8%B0%E0%B9%82%E0%B8%AD%E0%B8%99%E0%B9%80%E0%B8%87%E0%B8%B4%E0%B8%99%20%E0%B8%81%E0%B8%94%E0%B8%97%E0%B8%B5%E0%B9%88%E0%B8%99%E0%B8%B5%E0%B9%88&q=%3Ca%20i%3D0%3E%E0%B9%80%E0%B8%82%E0%B9%89%E0%B8%B2%E0%B8%8A%E0%B8%A1%3A%20%3C%2Fa%3E%3Ca%20i%3D1%3E31%3C%2Fa%3E%3Ca%20i%3D2%3E%E0%B8%84%E0%B8%A3%E0%B8%B1%E0%B9%89%E0%B8%87%3C%2Fa%3E&q=%3Ca%20i%3D0%3E%E0%B8%AD.%E0%B8%98%E0%B8%B1%E0%B8%8D%E0%B8%9A%E0%B8%B8%E0%B8%A3%E0%B8%B5%3C%2Fa%3E%3Ca%20i%3D1%3E%E0%B8%88.%E0%B8%9B%E0%B8%97%E0%B8%B8%E0%B8%A1%E0%B8%98%E0%B8%B2%E0%B8%99%E0%B8%B5%3C%2Fa%3E&q=21%20%E0%B8%9E.%E0%B8%A2.%202559%2011%3A20%20%E0%B8%99.&q=%E0%B8%96%E0%B8%B2%E0%B8%A1%E0%B8%94%E0%B9%88%E0%B8%A7%E0%B8%99&q=%E0%B8%AA%E0%B8%A7%E0%B8%B1%E0%B8%AA%E0%B8%94%E0%B8%B5&q=%3Ca%20i%3D0%3E%E0%B8%81%E0%B8%94%E0%B9%80%E0%B8%9E%E0%B8%B7%E0%B9%88%E0%B8%AD%E0%B9%82%E0%B8%97%E0%B8%A3%20%3C%2Fa%3E%3Ca%20i%3D1%3E0869987458%3C%2Fa%3E&q=%3Ca%20i%3D0%3E%E0%B9%80%E0%B8%82%E0%B9%89%E0%B8%B2%E0%B8%8A%E0%B8%A1%3A%20%3C%2Fa%3E%3Ca%20i%3D1%3E31%3C%2Fa%3E%3Ca%20i%3D2%3E%20%E0%B8%84%E0%B8%A3%E0%B8%B1%E0%B9%89%E0%B8%87%3C%2Fa%3E&q=%3Ca%20i%3D0%3E%E0%B9%82%E0%B8%97%E0%B8%A3%E0%B8%A8%E0%B8%B1%E0%B8%9E%E0%B8%97%E0%B9%8C%3C%2Fa%3E%3Ca%20i%3D1%3E%E0%B8%81%E0%B8%94%E0%B9%80%E0%B8%9E%E0%B8%B7%E0%B9%88%E0%B8%AD%E0%B9%82%E0%B8%97%E0%B8%A3%20%3C%2Fa%3E%3Ca%20i%3D2%3E0869987458%3C%2Fa%3E&q=%3Ca%20i%3D0%3E%E0%B8%AA%E0%B8%87%E0%B8%A7%E0%B8%99%E0%B8%A5%E0%B8%B4%E0%B8%82%E0%B8%AA%E0%B8%B4%E0%B8%97%E0%B8%98%E0%B8%B4%E0%B9%8C%20%C2%A9%20%3C%2Fa%3E%3Ca%20i%3D1%3E2559%3C%2Fa%3E%3Ca%20i%3D2%3E%20%20%E0%B8%9A%E0%B8%A3%E0%B8%B4%E0%B8%A9%E0%B8%B1%E0%B8%97%20%E0%B8%94%E0%B8%B5%E0%B9%80%E0%B8%AD%E0%B8%9F%20%E0%B8%A1%E0%B8%B2%E0%B8%A3%E0%B9%8C%E0%B9%80%E0%B8%81%E0%B9%87%E0%B8%95%E0%B9%80%E0%B8%9E%E0%B8%A5%E0%B8%AA%20%E0%B8%88%E0%B8%B3%E0%B8%81%E0%B8%B1%E0%B8%94%3C%2Fa%3E&q=%3Ca%20i%3D0%3E%E0%B8%8A%E0%B9%88%E0%B8%A7%E0%B8%A2%E0%B9%80%E0%B8%AB%E0%B8%A5%E0%B8%B7%E0%B8%AD%3C%2Fa%3E%3Ca%20i%3D1%3E%E0%B8%95%E0%B8%B4%E0%B8%94%E0%B8%95%E0%B9%88%E0%B8%AD%20Kaidee%3C%2Fa%3E%3Ca%20i%3D2%3E%E0%B8%A3%E0%B9%88%E0%B8%A7%E0%B8%A1%E0%B8%87%E0%B8%B2%E0%B8%99%E0%B8%81%E0%B8%B1%E0%B8%9A%E0%B9%80%E0%B8%A3%E0%B8%B2%3C%2Fa%3E%3Ca%20i%3D3%3E%E0%B8%99%E0%B9%82%E0%B8%A2%E0%B8%9A%E0%B8%B2%E0%B8%A2%E0%B8%84%E0%B8%A7%E0%B8%B2%E0%B8%A1%E0%B9%80%E0%B8%9B%E0%B9%87%E0%B8%99%E0%B8%AA%E0%B9%88%E0%B8%A7%E0%B8%99%E0%B8%95%E0%B8%B1%E0%B8%A7%3C%2Fa%3E%3Ca%20i%3D4%3E%E0%B8%99%E0%B9%82%E0%B8%A2%E0%B8%9A%E0%B8%B2%E0%B8%A2%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B9%83%E0%B8%8A%E0%B9%89%E0%B8%87%E0%B8%B2%E0%B8%99%3C%2Fa%3E&q=%3Ca%20i%3D0%3E%E0%B8%9D%E0%B9%88%E0%B8%B2%E0%B8%A2%E0%B8%9A%E0%B8%A3%E0%B8%B4%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B8%A5%E0%B8%B9%E0%B8%81%E0%B8%84%E0%B9%89%E0%B8%B2%3C%2Fa%3E%3Ca%20i%3D1%3E%20%E0%B8%88%E0%B8%B1%E0%B8%99%E0%B8%97%E0%B8%A3%E0%B9%8C%20-%20%E0%B8%AD%E0%B8%B2%E0%B8%97%E0%B8%B4%E0%B8%95%E0%B8%A2%E0%B9%8C%20%E0%B9%80%E0%B8%A7%E0%B8%A5%E0%B8%B2%207.00-23.00%20%E0%B8%99.%20%3C%2Fa%3E%3Ca%20i%3D2%3E%E0%B9%82%E0%B8%97%E0%B8%A3.%3C%2Fa%3E%3Ca%20i%3D3%3E02-119-5000%3C%2Fa%3E&q=%3Ca%20i%3D0%3EDownload%20iOS%20application%3C%2Fa%3E%3Ca%20i%3D1%3EDownload%20Android%20application%3C%2Fa%3E&q=%E0%B8%81%E0%B8%A5%E0%B8%B1%E0%B8%9A%E0%B8%94%E0%B9%89%E0%B8%B2%E0%B8%99%E0%B8%9A%E0%B8%99&q=%3Ca%20i%3D0%3E%E0%B9%80%E0%B8%8A%E0%B8%B4%E0%B8%8D%E0%B8%95%E0%B8%AD%E0%B8%9A%E0%B9%81%E0%B8%9A%E0%B8%9A%E0%B8%AA%E0%B8%AD%E0%B8%9A%E0%B8%96%E0%B8%B2%E0%B8%A1%E0%B9%80%E0%B8%9E%E0%B8%B7%E0%B9%88%E0%B8%AD%E0%B9%83%E0%B8%AB%E0%B9%89%E0%B8%81%E0%B8%B3%E0%B8%A5%E0%B8%B1%E0%B8%87%E0%B9%83%E0%B8%88%20Kaidee%20%E0%B8%82%E0%B8%AD%E0%B8%87%E0%B9%80%E0%B8%A3%E0%B8%B2%E0%B8%84%E0%B9%88%E0%B8%B0%3C%2Fa%3E%3Ca%20i%3D1%3E%E0%B8%95%E0%B8%81%E0%B8%A5%E0%B8%87%3C%2Fa%3E%3Ca%20i%3D2%3E%E0%B8%84%E0%B8%A3%E0%B8%B2%E0%B8%A7%E0%B8%AB%E0%B8%99%E0%B9%89%E0%B8%B2%3C%2Fa%3E&q=Close%20(Esc)&q=Share&q=Toggle%20fullscreen&q=Zoom%20in%2Fout&q=Previous%20(arrow%20left)&q=Next%20(arrow%20right)"""

      val request = url("https://translate.googleapis.com/translate_a/t?anno=3&client=te_lib&format=html&v=1.0&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw&logld=v49&sl=th&tl=en&tc=2&tk=628485.1036324&mode=1")
        .setContentType("application/x-www-form-urlencoded", "UTF-8")
        .setBody(q)

      for (result <- Http(request > as.String)) {
        logger.info(result)
      }

    case Translate(text, from, to) =>
      val request = url("https://translate.google.com/translate_a/single?client=t&tk=843101.694885&dt=t")
        .addQueryParameter("tl", to)
        .addQueryParameter("sl", from.getOrElse("auto"))
        .addQueryParameter("q", text)

      for (result <- Http(request > as.String)) {
        val translation = """\[\[\[\"(.+?)\".+""".r
        result match {
          case translation(txt) => sender() ! txt
          case _ =>
            logger.warn("Can't parse {}", result)
            sender() ! text
        }
      }
  }
}
