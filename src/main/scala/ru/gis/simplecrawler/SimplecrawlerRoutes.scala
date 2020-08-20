package ru.gis.simplecrawler

import cats.data.NonEmptyList
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.RichHttp4sHttpEndpoint

object SimplecrawlerRoutes {

  def crawlerRoutes[F[_]: Sync](C: Crawler[F], endpoints: Endpoints)(implicit CS: ContextShift[F]): HttpRoutes[F] = {
    def getTitle(url: String): F[Either[Unit, Crawler.WebsiteWithTitle]] = {
      val websiteWithTitle = C.getWebsiteWithTitle(Crawler.Website(decodeUrl(url)))
      websiteWithTitle.map(_.asRight[Unit])
    }

    def getTitles(urls: List[String]): F[Either[Unit, List[Crawler.WebsiteWithTitle]]] = {
      val websiteWithTitle = urls.traverse(url => C.getWebsiteWithTitle(Crawler.Website(decodeUrl(url))))
      websiteWithTitle.map(_.asRight[Unit])
    }

    def decodeUrl(url: String) = {
      import java.nio.charset.StandardCharsets
      java.net.URLDecoder.decode(url, StandardCharsets.UTF_8.name)
    }

    val value: NonEmptyList[HttpRoutes[F]] = NonEmptyList
      .of(
        endpoints.getTitleEndpoint.toRoutes(logic = getTitle),
        endpoints.retrieveTitlesEndpoint.toRoutes(logic = getTitles)
      )

    value.reduceK
  }

}