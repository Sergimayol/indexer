package com.indexer.main

import akka.actor.ActorSystem
import com.indexer.services.storage.db.IndexerContentServiceDBImpl
import com.indexer.stream.flows.{ContentParserIndexerFlow, MetadataFileExtractorFlow, ReadFileStreamFlow}
import com.indexer.stream.pipelines.IndexerPipeline
import com.indexer.stream.sinks.IndexerStorageSink
import com.indexer.stream.sources.FileStreamSource
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global

object Main {
  implicit val system: ActorSystem = ActorSystem("indexer-pipeline-system")

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load()
    val fileSource = FileStreamSource(config.getString("indexer.initialPath"))

    val metadataFileExtractorFlow = MetadataFileExtractorFlow()

    val readFileFlow = ReadFileStreamFlow()

    val contentIndexerFlow = ContentParserIndexerFlow()

    val indexerContentService = IndexerContentServiceDBImpl()

    val indexerSink = IndexerStorageSink(indexerContentService, config.getInt("indexer.insertBatchSize"))

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
