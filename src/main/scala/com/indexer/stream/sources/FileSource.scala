package com.indexer.stream.sources

import akka.{Done, NotUsed}
import akka.stream.scaladsl.Source

import java.io.File
import scala.concurrent.Future

trait FileSource {
  def getSource: Source[File, Future[Done]]
}
