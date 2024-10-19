package com.indexer.stream.pipelines

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.indexer.stream.flows.{ContentIndexerFlow, MetadataFileFlow, ReadFileFlow}
import com.indexer.stream.sinks.IndexerSink
import com.indexer.stream.sources.FileSource

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class IndexerPipeline(
    fileSource: FileSource,
    metadataFileExtractorFlow: MetadataFileFlow,
    readFileFlow: ReadFileFlow,
    contentIndexerFlow: ContentIndexerFlow,
    indexerSink: IndexerSink
)(implicit val ec: ExecutionContext, system: ActorSystem)
    extends Pipeline {

  override def run(): Unit = {
    val pipeline =
      fileSource.getSource
        .via(metadataFileExtractorFlow.getFlow)
        .via(readFileFlow.getFlow)
        .via(contentIndexerFlow.getFlow)
        .to(indexerSink.getSink)

    pipeline.run().onComplete {
      case Success(_) =>
        println("Pipeline execution run was successful")
        system.terminate()
      case Failure(ex) =>
        println(s"An error occurred during pipeline execution: ${ex.getMessage}")
        system.terminate()
    }
  }
}
