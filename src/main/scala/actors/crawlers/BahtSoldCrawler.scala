package actors.crawlers

import akka.actor.Actor
import dispatch.{Http, as, url}
import jodd.jerry.{Jerry, JerryFunction}
import org.joda.time.DateTime

import scala.collection.mutable
import scala.util.Try

object BahtSoldCrawler {

}

/**
  * Crawler for www.bahtsold.com
  */
class BahtSoldCrawler() extends Actor {
  implicit val ec = context.dispatcher

  override def receive: Receive = {
    case Request(search, minPrice, maxPrice, pageNumber, _) =>
      val request = url("http://www.bahtsold.com/quicksearch2")
        .addQueryParameter("co", "Thailand-1")
        .addQueryParameter("ca", "9")    //  Category=Motocycle for sale
        .addQueryParameter("c1", "377")  //  SubCategory=Motocycle for sale; c2=691 - 500-999cc
        .addQueryParameter("c", "1005")  //  ???
        .addQueryParameter("s", search)
        .addQueryParameter("pr_from", minPrice.toString)
        .addQueryParameter("pr_to", maxPrice.toString)
        .addQueryParameter("sort", "9")  //  Active ads first
        .addQueryParameter("_p", pageNumber.getOrElse(1).toString)

      val currentSender = sender

      for (htmlResult <- Http(request > as.String)) {
        val htmlDoc = Jerry.jerry(htmlResult)
        val results = new mutable.ListBuffer[ResultItem]
        htmlDoc.$("#height_main_color .rows").each(new JerryFunction() {
          override def onNode($this: Jerry, index: Int): java.lang.Boolean = {
            val resultImg = $this.$(".image a")

            val url = resultImg.attr("href")
            val img = resultImg.$("img").attr("src")

            val title = $this.$(".offer h5").text()

            //val date = extractDate($this.$(".result-date").attr("datetime"))
            val priceText = $this.$(".description p").last().text().replaceAll(" THB", "").replaceAll(",", "")

            val price = Try(priceText.toInt).toOption
            val location = ""//extractLocation(meta.$(".result-hood").text())
            val date = DateTime.now()

            price.foreach { p =>
              val item = ResultItem(url, Seq(img), title, p, date, location)
              results.append(item)
            }

            true
          }
        })

        val items = results.filter(x => x.price >= minPrice && x.price <= maxPrice)
        currentSender ! ProviderResult("bahtsold", items)
      }
  }
}
