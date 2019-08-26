package actors.crawlers

import java.text.SimpleDateFormat

import akka.actor.{Actor, ActorRef}
import com.typesafe.scalalogging.LazyLogging
import dispatch._
import org.joda.time.DateTime
import org.json4s._
import org.json4s.jackson.JsonMethods._

object Kaidee {
  case class Result(total: Int, items: Seq[Item])

  case class Item(ad: Ad)

  case class Ad(
                 description: Option[String],
                 first_approved_time: Option[String],
                 images: Seq[ItemImage],
                 price: Option[Int],
                 title: Option[String],
                 legacy_id: Option[Long]
               ) {
    val url = s"https://www.kaidee.com/product-${legacy_id.getOrElse("")}"
    val place = ""//s"${location.district.name} (${location.province.name})"
  }

  case class ItemImage(sizes: ImageSizes)
  case class ImageSizes(large: Image, medium: Image, thumb: Image)
  case class Image(link: String)
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
    case Request(query, minPrice, maxPrice, pageNumber, _) =>
      val request = url("https://api.kaidee.com/0.7/ads/search")
        .POST
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "*/*")
        .addHeader("Referer", "https://www.kaidee.com/c12-motorcycle-motorcycle/bangkok/")
        .addHeader("uuid", "01e5cb1c5bba45e9bcbb5b3f57c3bceb") <<
        s"""{"page":${pageNumber.getOrElse(1)},"limit":100,"cate_id":12,"province_id":9,"district_id":0,"q":"$query","attribute":[],"price":{"search_type":"range","from":$minPrice,"to":$maxPrice}}"""
      val currentSender = sender

      for (jsonResult <- Http(request > as.String)) {
        val json = parse(jsonResult)
        val result = json.extract[Kaidee.Result]

        val items = result.items.map(_.ad)
          .filter(_.price.isDefined)
          .filter(x => x.price.get >= minPrice && x.price.get <= maxPrice)
          .map { item =>
            val imgs = item.images.map(_.sizes.medium.link)
            ResultItem(item.url, imgs, item.title.getOrElse(""), item.description.getOrElse(""), item.price.get, DateTime.now, item.place)
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
