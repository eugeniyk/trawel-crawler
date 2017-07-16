package api

import actors.Supervisor
import actors.crawlers.{JsonResultFormats, ProviderResult, Request, Response}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.HttpOriginRange
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, Route}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by ekalashnikov on 7/15/17.
  */
object Api extends JsonResultFormats {

  def init(implicit actorSystem: ActorSystem): Unit = {
    val supervisor = actorSystem.actorOf(Props[Supervisor])

    implicit val materializer = ActorMaterializer()
    implicit val executionContext = actorSystem.dispatcher

    val uiRoute: Route = get {
      getFromResourceDirectory("web")
    }

    val apiRoute: Route =
      path("api" / "search") {
        get {
          parameters(('q.as[String], 'priceMin.as[Int] ?, 'priceMax.as[Int] ?)) { case (query, min, max) =>
            val request = Request(query, min.getOrElse(0), max.getOrElse(Int.MaxValue))

            complete {
              implicit val timeout = Timeout(1.minute)
              (supervisor ? request).mapTo[Response]
            }
          }
        }
      }

    val allRoutes = uiRoute ~ addCORS(apiRoute)
    val bindingFuture = Http().bindAndHandle(allRoutes, "localhost", 8080)
  }

  private def addCORS(route: Route): Route = {
    val settings = CorsSettings.defaultSettings.copy(
      allowGenericHttpRequests = true,
      allowCredentials = false,
      allowedOrigins = HttpOriginRange.*)

    val routeWithCORS = handleRejections(CorsDirectives.corsRejectionHandler) {
      cors(settings) {
        handleRejections(RejectionHandler.default) { route }
      }
    }

    routeWithCORS
  }
}