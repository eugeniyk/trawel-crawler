package actors.crawlers

import org.joda.time.DateTime

/**
  * Created by ekalashnikov on 12/11/2016 AD.
  */
case class Request(query: String = "", minPrice: Int = 0, maxPrice: Int = Int.MaxValue, pageNumber: Option[Int] = None)

case class Result(items: Seq[ResultItem])

case class ResultItem(url: String, title: String, price: Int, lastDate: DateTime)