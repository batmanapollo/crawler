package ru.gis.simplecrawler

import sttp.tapir._
import sttp.tapir.docs.openapi._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.{Endpoint, endpoint, path}
import sttp.tapir.openapi.Info

class Endpoints(val basePath: String) {

  private lazy val info = Info(
    "Simple Crawler",
    "0.1",
  )

  def openApiYaml: String = List(
    getTitleEndpoint,
    retrieveTitlesEndpoint
  ).toOpenAPI(info).toYaml

  lazy val getTitleEndpoint: Endpoint[String, Unit, Crawler.WebsiteWithTitle, Nothing] =
    endpoint.get
      .in(basePath / "title" / path[String]("url")).description("url, slash must be defined as %2F")
      .out(jsonBody[Crawler.WebsiteWithTitle])
      .description("Get website with title by url")

  lazy val retrieveTitlesEndpoint: Endpoint[List[String], Unit, List[Crawler.WebsiteWithTitle], Nothing] =
    endpoint.post
      .in(basePath / "title")
      .in(jsonBody[List[String]]).description("List of urls")
      .out(jsonBody[List[Crawler.WebsiteWithTitle]])
      .description("Retrieve all websites' titles by urls")

}
