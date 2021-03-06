package actors.crawlers

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import dispatch.{Http, as, url}
import jodd.jerry.{Jerry, JerryFunction}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable
import scala.util.Try

/**
  * Created by ekalashnikov on 12/11/2016 AD.
  */

object CraiglistCrawler {
  private val locationRg = ".*\\((.*)\\).*".r
  private[crawlers] def extractLocation(input: String): String = {
    input match {
      case locationRg(loc) => loc
      case _ => ""
    }
  }

  private[crawlers] def extractPrice(input: String): Option[Int] = {
    Try(input.substring(1).toInt).toOption
  }

  private[crawlers] def extractDate(input: String): DateTime = {
    DateTime.parse(input, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"))
  }

  private val imgFormat = "http://images.craigslist.org/%s_300x300.jpg"
  private[crawlers] def extractImgs(input: String): Seq[String] = {
    Option(input).getOrElse("").split(",").withFilter(_.nonEmpty).map(_.substring(2)).map(imgFormat.format(_))
  }
}

class CraiglistCrawler extends Actor with LazyLogging {
  import CraiglistCrawler._
  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case Request(search, minPrice, maxPrice, pageNumber, _) =>
      val request = url("https://bangkok.craigslist.org/search/mca")
          .addParameter("query", search)
          .addParameter("s", ((pageNumber.getOrElse(1)-1)*100).toString)
          .addParameter("min_price", minPrice.toString)
          .addParameter("max_price", maxPrice.toString)
      val currentSender = sender

      for (htmlResult <- Http(request > as.String)) {
        val htmlDoc = Jerry.jerry(htmlResult)
        val results = new mutable.ListBuffer[ResultItem]
        htmlDoc.$("#searchform .result-row").each(new JerryFunction() {
          override def onNode($this: Jerry, index: Int): java.lang.Boolean = {
            val resultImg = $this.$("a.result-image")

            val url = s"https://bangkok.craigslist.org${resultImg.attr("href")}"

            val imgs = extractImgs(resultImg.attr("data-ids"))
            val title = $this.$(".result-title").text()
            val meta = $this.$(".result-meta")
            val date = extractDate($this.$(".result-date").attr("datetime"))
            val price = extractPrice(meta.$(".result-price").text())
            val location = extractLocation(meta.$(".result-hood").text())

            price.foreach { p =>
              val item = ResultItem(url, imgs, title, "", p, date, location)
              results.append(item)
            }

            true
          }
        })

        val items = results.filter(x => x.price >= minPrice && x.price <= maxPrice)
        currentSender ! ProviderResult("craiglist", items)
      }
  }
}