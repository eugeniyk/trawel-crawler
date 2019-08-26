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
      val request = url("https://www.bahtsold.com/quicksearch2")
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
        htmlDoc.$(".items-list article").each(new JerryFunction() {
          override def onNode($this: Jerry, index: Int): java.lang.Boolean = {
            val resultLink = $this.$("a.imgAsBg")

            val url = resultLink.attr("href")
            val img = $this.$("img").attr("src")

            val title = resultLink.attr("title")

            //val date = extractDate($this.$(".result-date").attr("datetime"))
            val description = $this.$(".item-content .inner p").text()

            val price: Option[Int] = Try {
              $this.$(".price-tag").text()
              .replaceAll("THB", "")
              .replaceAll(",", "")
              .replaceAll(" ", "")
              .replaceAll("\n", "")
              .toInt
            }.toOption

            val location = ""//extractLocation(meta.$(".result-hood").text())
            val date = DateTime.now()

            price.foreach { p =>
              val item = ResultItem(url, Seq(img), title, description, p, date, location)
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
