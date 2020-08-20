package ru.gis.simplecrawler

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import sttp.tapir.redoc.http4s.RedocHttp4s

import scala.concurrent.ExecutionContext.global

object SimpleCrawlerServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      crawlerAlg = Crawler.impl[F]
      endpoints = new Endpoints(basePath = "crawler")

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        SimpleCrawlerRoutes.crawlerRoutes[F](crawlerAlg, endpoints) <+>
        new RedocHttp4s("Simple Crawler docs", endpoints.openApiYaml).routes[F]
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      httpInterface = "localhost"
      httpPort      = 8080

      _ = println(s"Visit http://$httpInterface:$httpPort to see ReDoc API documentation")

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(httpPort, httpInterface)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
