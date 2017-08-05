package actors.crawlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.joda.time.DateTime
import spray.json._

/**
  * Created by ekalashnikov on 12/11/2016 AD.
  */
case class Request(query: String = "",
                   minPrice: Int = 0,
                   maxPrice: Int = Int.MaxValue,
                   pageNumber: Option[Int] = None,
                   sources: Set[String] = Set.empty)

case class Response(results: Seq[ProviderResult])
case class ProviderResult(provider: String, items: Seq[ResultItem])
case class ResultItem(url: String, imgs: Seq[String], title: String, price: Int, lastDate: DateTime, location: String)

trait JsonResultFormats extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val jodaDateTimeFormat = new RootJsonFormat[DateTime] {
    def write(value: DateTime) = JsString(value.toString())
    def read(value: JsValue): DateTime = value match {
      case JsString(s) =>
        try DateTime.parse(s)
        catch {
          case e: Throwable => deserializationError("Couldn't convert '" + s + "' to a date-time: " + e.getMessage)
        }
      case s => deserializationError("Couldn't convert '" + s + "' to a date-time")
    }
  }

  implicit val resultItemFormat = jsonFormat6(ResultItem)
  implicit val providerResultFormat = jsonFormat2(ProviderResult)
  implicit val responseFormat = jsonFormat1(Response)
}