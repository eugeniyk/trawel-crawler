package actors.crawlers

import java.lang.Boolean

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

class CraiglistCrawler extends Actor with LazyLogging {
  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case Request(search, minPrice, maxPrice, pageNumber) =>
      val request = url("http://bangkok.craigslist.co.th/search/mcy")
          .addParameter("auto_transmission", "1")
          .addParameter("query", search)
          .addParameter("s", ((pageNumber.getOrElse(1)-1)*100).toString)
      val currentSender = sender

      for (jsonResult <- Http(request > as.String)) {
        val htmlDoc = Jerry.jerry(jsonResult)
        val results = new mutable.ListBuffer[ResultItem]
        htmlDoc.$("#searchform .result-row").each(new JerryFunction() {
          override def onNode($this: Jerry, index: Int): Boolean = {
            val url = s"http://bangkok.craigslist.co.th${$this.$("a.result-image").attr("href")}"
            val date = DateTime.parse($this.$(".result-date").attr("datetime"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"))
            val title = $this.$(".result-title").text()
            val price = Try($this.$(".result-meta .result-price").text().substring(1).toInt).toOption

            if (price.isDefined) {
              results.append(ResultItem(url, title, price.get, date))
            }
            true
          }
        })

        val items = results.filter(x => x.price >= minPrice && x.price <= maxPrice)
        currentSender ! Result(items)
      }
  }
}