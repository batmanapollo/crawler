package ru.gis.simplecrawler

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    SimpleCrawlerServer.stream[IO].compile.drain.as(ExitCode.Success)
}