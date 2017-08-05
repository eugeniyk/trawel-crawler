package actors.crawlers

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging.LazyLogging
import dispatch._
import org.joda.time.DateTime
import org.json4s._
import org.json4s.jackson.JsonMethods._

object Kaidee {
  case class Result(total: Int, items: Seq[Item])
  case class Item(item_id: Long,
                  item_price: Int,
                  item_topic: String,
                  location: Location,
                  photos: Seq[Photo],
                  post_date: Date,
                  last_action_date: Date
                 ) {
    val url = s"https://www.kaidee.com/product-$item_id"
    val place = s"${location.district.name} (${location.province.name})"
  }

  case class Photo(large: String, medium: String, thumb: Option[String])
  case class Location(district: Place, province: Place)
  case class Place(id: Int, name: String)
}

/**
  * Created by ekalashnikov on 12/7/2016 AD.
  */
class KaideeCrawler(translator: ActorRef) extends Actor with LazyLogging {
  implicit val ec = context.dispatcher

  implicit val formats = new org.json4s.DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  }

  override def receive: Receive = {
    case Request(query, minPrice, maxPrice, pageNumber) =>
      val request = url("https://profx.24x7th.com/v5/search/listing")
        .POST
        .addHeader("publicToken", "lBOlvDZA2IcG3E1St6gwTTAETIXvZ2XCGnyE+z+2sck=")
        .addHeader("memberId", "6004137")
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "*/*")
        .addHeader("Referer", "https://www.kaidee.com/c12p9-motorcycle-motorcycle/bangkok/")
        .addHeader("uuid", "01e5cb1c5bba45e9bcbb5b3f57c3bceb") <<
        s"""{"page":${pageNumber.getOrElse(1)},"limit":300,"cate_id":12,"province_id":9,"district_id":0,"facet":"ATTR_1","q":"$query","item_type":"","condition":"","attribute":[]}"""

      val currentSender = sender

      for (jsonResult <- Http(request > as.String)) {
        val json = parse(jsonResult)
        val result = json.extract[Kaidee.Result]

        val items = result.items
          .filter(x => x.item_price >= minPrice && x.item_price <= maxPrice)
          .map { item =>
            val imgs = item.photos.map(_.medium)
            ResultItem(item.url, imgs, item.item_topic, item.item_price, new DateTime(item.last_action_date), item.place)
          }

        currentSender ! ProviderResult("kaidee", items)

//        val translations = itemMap.map { case (id, item) =>
//          import akka.pattern.ask
//          import scala.concurrent.duration._
//          implicit val timeout = Timeout(1 second)
//
//          (translator ? Translate(item.item_topic, Some("th"), "en")).mapTo[String]
//            .recover { case e: Throwable => item.item_topic }
//            .map { translation =>
//              item.copy(item_topic = translation)
//            }
//        }
//
//        for (translatedResult <- Future.sequence(translations)) {
//          logger.info(translatedResult.toString())
//        }

      }

    case requestUrl: String =>
      val request = url(requestUrl).POST
      for(result <- Http(request > as.String))
        logger.info(result)
  }
}
