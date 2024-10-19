package com.indexer.stream.sinks

import akka.Done
import akka.stream.scaladsl.Sink
import com.indexer.models.IndexerContent

import scala.concurrent.Future

trait IndexerSink {
  def getSink: Sink[IndexerContent, Future[Done]]
}
