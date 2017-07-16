package actors.crawlers

import org.joda.time.DateTime
import org.specs2.mutable.Specification

class CraiglistCrawlerSpec extends Specification {
  "Craiglist Crawler" should {
    "extract location correctly" in {
      val valueFromSite = " (BANGKOK)"
      CraiglistCrawler.extractLocation(valueFromSite) should_== "BANGKOK"
      CraiglistCrawler.extractLocation("") should_== ""
      CraiglistCrawler.extractLocation("some") should_== ""
    }

    "extract price correctly" in {
      val valueFromSite = "฿500"
      CraiglistCrawler.extractPrice(valueFromSite) should_== Some(500)
      CraiglistCrawler.extractPrice("") should_== None
      CraiglistCrawler.extractPrice("some") should_== None
    }

    "extract date correctly" in {
      val valueFromSite = "2017-07-15 19:03"
      CraiglistCrawler.extractDate(valueFromSite) should_== new DateTime(2017, 7, 15, 19, 3)
    }

    "extract imgs correctly" in {
      val expected = "http://images.craigslist.org/00909_cZWZPgK6SRV_300x300.jpg"
      val dataIds = "1:00909_cZWZPgK6SRV,1:00W0W_44IWTIz7mpk,1:00Q0Q_7CWlzdjbG2K,1:00b0b_jxNNriLPKz1,1:00B0B_68k1qGYOwXF,1:00f0f_562IebnvXps"
      val imgs = CraiglistCrawler.extractImgs(dataIds)
      imgs.size should_== 6
      imgs.headOption should_== Some(expected)
      CraiglistCrawler.extractImgs(null) should_== Seq()
    }
  }
}