package com.indexer.main

import akka.actor.ActorSystem
import com.indexer.services.storage.db.IndexerContentServiceDBImpl
import com.indexer.stream.flows.{ContentParserIndexerFlow, MetadataFileExtractorFlow, ReadFileStreamFlow}
import com.indexer.stream.pipelines.IndexerPipeline
import com.indexer.stream.sinks.IndexerStorageSink
import com.indexer.stream.sources.FileStreamSource

import scala.concurrent.ExecutionContext.Implicits.global

object Main {
  implicit val system: ActorSystem = ActorSystem("indexer-pipeline-system")

  def main(args: Array[String]): Unit = {
    val fileSource = FileStreamSource("./")

    val metadataFileExtractorFlow = MetadataFileExtractorFlow()

    val readFileFlow = ReadFileStreamFlow()

    val contentIndexerFlow = ContentParserIndexerFlow()

    val indexerContentService = IndexerContentServiceDBImpl()
   
    val indexerSink = IndexerStorageSink(indexerContentService)

    val pipeline = IndexerPipeline(
      fileSource,
      metadataFileExtractorFlow,
      readFileFlow,
      contentIndexerFlow,
      indexerSink
    )

    pipeline.run()
  }
}
