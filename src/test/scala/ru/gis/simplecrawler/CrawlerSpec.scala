package ru.gis.simplecrawler

import cats.effect.{ContextShift, IO}
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

import scala.concurrent.ExecutionContext

class CrawlerSpec extends org.specs2.mutable.Specification {

  "Get one title" >> {
    "return 200" >> {
      uriReturns200(retGetTitle)
    }
    "return one website" >> {
      uriReturnsWebsiteWithTitle()
    }

  }

  "Get several titles" >> {
    "return 200" >> {
      uriReturns200(regGetTitles)
    }
    "return two websites" >> {
      uriReturnsTwoWebsitesWithTitle()
    }

  }

  private val executionContext: ExecutionContext      = scala.concurrent.ExecutionContext.Implicits.global
  private implicit val contextShift: ContextShift[IO] = IO.contextShift(executionContext)
  private val endpoints                               = new Endpoints(basePath = "crawler")

  private[this] val retGetTitle: Response[IO] = {
    val getHW = Request[IO](Method.GET, uri"crawler/title/https:%2F%2Fcontent.2gis.ru")
    val crawler = Crawler.impl[IO]
    SimpleCrawlerRoutes.crawlerRoutes(crawler, endpoints).orNotFound(getHW).unsafeRunSync()
  }

  private[this] def uriReturns200(response: Response[IO]): MatchResult[Status] =
    response.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsWebsiteWithTitle(): MatchResult[String] = {
    val expected = "{\"website\":\"https://content.2gis.ru\",\"title\":\"Контент-продукты\"}"
    retGetTitle.as[String].unsafeRunSync() must beEqualTo(expected)
  }

  private[this] val regGetTitles: Response[IO] = {
    val body = List("https://content.2gis.ru", "https://www.google.com")
    val getHW = Request[IO](Method.POST, uri"crawler/title").withEntity(body)
    val crawler = Crawler.impl[IO]
    SimpleCrawlerRoutes.crawlerRoutes(crawler, endpoints).orNotFound(getHW).unsafeRunSync()
  }

  private[this] def uriReturnsTwoWebsitesWithTitle(): MatchResult[String] = {
    val expectedOne = "{\"website\":\"https://www.google.com\",\"title\":\"Google\"}"
    val expectedTwo = "{\"website\":\"https://content.2gis.ru\",\"title\":\"Контент-продукты\"}"

    val expected = List(expectedTwo, expectedOne).mkString("[", ",", "]")

    regGetTitles.as[String].unsafeRunSync() must beEqualTo(expected)
  }

}