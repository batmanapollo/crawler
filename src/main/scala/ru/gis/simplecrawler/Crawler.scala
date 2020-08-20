package ru.gis.simplecrawler

import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import io.circe.Decoder.Result
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import org.jsoup.Jsoup

trait Crawler[F[_]]{
  def getWebsiteWithTitle(n: Crawler.Website): F[Crawler.WebsiteWithTitle]
}

object Crawler {
  implicit def apply[F[_]](implicit ev: Crawler[F]): Crawler[F] = ev

  final case class Website(url: String) extends AnyVal
  final case class WebsiteWithTitle(website: Website, title: Option[String], error: Option[String])

  object WebsiteWithTitle {
    implicit val websiteWithTitleEncoder: Encoder[WebsiteWithTitle] = new Encoder[WebsiteWithTitle] {
      override def apply(a: WebsiteWithTitle): Json = Json.obj(
        ("website", a.website.url.asJson),
        ("title", a.title.asJson),
        ("error", a.error.asJson)
      ).dropNullValues
    }

    implicit val websiteWithTitleDecoder: Decoder[WebsiteWithTitle] = new Decoder[WebsiteWithTitle] {
      override def apply(c: HCursor): Result[WebsiteWithTitle] =
        for {
          website <- c.downField("website").as[String]
          title <- c.downField("title").as[String]
          error <- c.downField("error").as[String]
        } yield {
          new WebsiteWithTitle(Website(website), Option(title), Option(error))
        }
    }

    implicit def websiteWithTitleEntityEncoder[F[_]: Applicative]: EntityEncoder[F, WebsiteWithTitle] =
      jsonEncoderOf[F, WebsiteWithTitle]
  }

  def impl[F[_]: Applicative]: Crawler[F] = new Crawler[F]{
    def getWebsiteWithTitle(n: Crawler.Website): F[Crawler.WebsiteWithTitle] = {
      try  {
        WebsiteWithTitle(n, Some(Jsoup.connect(n.url).get().title()), None).pure[F]
      } catch {
        case _: Throwable => WebsiteWithTitle(n, None, Some("Не удалось выполнить запрос")).pure[F]
      }
    }
  }

}

